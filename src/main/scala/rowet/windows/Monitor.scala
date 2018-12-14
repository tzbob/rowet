package rowet.windows

import cats.effect.IO
import com.sun.jna.platform.win32.{User32, WinDef, WinUser}
import com.sun.jna.platform.win32.WinUser.{HMONITOR, MONITORENUMPROC}
import rowet.{Geometry, MonitorCompanion}

import scala.collection.mutable.ListBuffer

case class Monitor(private[windows] val hMonitor: HMONITOR,
                   private[windows] val geometry0: Geometry)
    extends rowet.Monitor

object Monitor extends MonitorCompanion[Window, Monitor, IO] {
  override val monitors: IO[List[Monitor]] = IO {
    val callback = new MonitorEnumeratorCallback
    User32.INSTANCE.EnumDisplayMonitors(null, null, callback, null)
    callback.monitors()
  }

  override val windows: IO[Monitor => List[Window]] = for {
    windows <- Window.windows
  } yield { m: Monitor =>
    {
      windows.filter { window =>
        m.hMonitor == User32.INSTANCE
          .MonitorFromWindow(window.hWND, WinUser.MONITOR_DEFAULTTONEAREST)
      }
    }
  }

  override val geometry: IO[Monitor => Geometry] = IO.pure(w => w.geometry0)

  class MonitorEnumeratorCallback extends MONITORENUMPROC {
    private[this] val monitorBuffer = ListBuffer.empty[Monitor]
    override def apply(hMonitor: WinUser.HMONITOR,
                       hdcMonitor: WinDef.HDC,
                       lprcMonitor: WinDef.RECT,
                       dwData: WinDef.LPARAM): Int = {
      val rec = lprcMonitor.toRectangle
      monitorBuffer += new Monitor(
        hMonitor,
        Geometry(rec.x, rec.y, rec.height, rec.width))
      1
    }
    def monitors(): List[Monitor] = monitorBuffer.toList
  }
}
