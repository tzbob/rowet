package rowet.internal.windows

import cats.effect.IO
import com.sun.jna.platform.win32.WinUser.{HMONITOR, MONITORENUMPROC}
import com.sun.jna.platform.win32.{User32, WinDef, WinUser}
import rowet.internal.{Geometry, MonitorCompanion}

import scala.collection.mutable.ListBuffer

case class Monitor(private[windows] val hMonitor: HMONITOR, geometry: Geometry)
    extends rowet.internal.Monitor

object Monitor extends MonitorCompanion[Window, Monitor, IO] {
  override val monitors: IO[List[Monitor]] = IO {
    val callback = new MonitorEnumeratorCallback
    User32.INSTANCE.EnumDisplayMonitors(null, null, callback, null)
    callback.monitors()
  }

  override def windows(monitor: Monitor): IO[List[Window]] =
    for {
      windows <- Window.windows
    } yield {
      windows.filter { window =>
        monitor.hMonitor == User32.INSTANCE
          .MonitorFromWindow(window.hWND, WinUser.MONITOR_DEFAULTTONEAREST)
      }
    }

  class MonitorEnumeratorCallback extends MONITORENUMPROC {
    private[this] val monitorBuffer = ListBuffer.empty[Monitor]
    override def apply(hMonitor: WinUser.HMONITOR,
                       hdcMonitor: WinDef.HDC,
                       lprcMonitor: WinDef.RECT,
                       dwData: WinDef.LPARAM): Int = {
      val rec = lprcMonitor.toRectangle
      monitorBuffer += new Monitor(
        hMonitor,
        Geometry(rec.x, rec.y, rec.width, rec.height))
      1
    }
    def monitors(): List[Monitor] = monitorBuffer.toList
  }
}
