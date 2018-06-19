package rowet.windows

import cats.effect.IO
import com.sun.jna.platform.win32.{User32, WinDef, WinUser}
import com.sun.jna.platform.win32.WinUser.{HMONITOR, MONITORENUMPROC}
import rowet.{Geometry, MonitorCompanion}

import scala.collection.mutable.ListBuffer

case class Monitor(private[windows] val hMonitor: HMONITOR, private[windows] val geometry0: Geometry)
    extends rowet.Monitor[IO] {
  override val windows: IO[List[Window]] = for {
    windows <- Window.windows
  } yield {
    windows.filter { window =>
      hMonitor == User32.INSTANCE
        .MonitorFromWindow(window.hWND, WinUser.MONITOR_DEFAULTTONEAREST)
    }
  }

  override val geometry: IO[Geometry] = IO.pure(geometry0)
}

object Monitor extends MonitorCompanion[IO] {
  override val monitors: IO[List[rowet.Monitor[IO]]] = IO {
    val callback = new MonitorEnumeratorCallback
    User32.INSTANCE.EnumDisplayMonitors(null, null, callback, null)
    callback.monitors()
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
        Geometry(rec.x, rec.y, rec.height, rec.width))
      1
    }
    def monitors(): List[Monitor] = monitorBuffer.toList
  }
}
