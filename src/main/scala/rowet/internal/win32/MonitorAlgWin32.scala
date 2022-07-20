package rowet.internal.win32

import cats.effect.{IO, Sync}
import cats.implicits.*
import com.sun.jna.platform.win32.WinUser.{HMONITOR, MONITORENUMPROC}
import com.sun.jna.platform.win32.{User32, WinDef, WinUser}
import rowet.domain.Rectangle
import rowet.internal.win32.MonitorAlgWin32.Monitor
import rowet.internal.win32.WindowAlgWin32.Window
import rowet.internal.win32.ffi.WinApi
import rowet.internal.{MonitorAlg, WindowAlg}

import scala.collection.mutable.ListBuffer
import scala.language.unsafeNulls

object MonitorAlgWin32:
  case class Monitor(private[win32] val hMonitor: HMONITOR, rectangle: Rectangle) extends rowet.domain.Monitor

class MonitorAlgWin32[F[_]](W: WindowAlg[Window, F])(implicit F: Sync[F]) extends MonitorAlg[Window, Monitor, F]:
  override val monitors: F[List[Monitor]] = {
    class MonitorEnumeratorCallback extends MONITORENUMPROC:
      private[this] val monitorBuffer = ListBuffer.empty[Monitor]
      override def apply(
          hMonitor: WinUser.HMONITOR,
          hdcMonitor: WinDef.HDC,
          lprcMonitor: WinDef.RECT,
          dwData: WinDef.LPARAM
      ): Int =
        val rec = lprcMonitor.toRectangle
        monitorBuffer += Monitor(hMonitor, Rectangle(rec.x, rec.y, rec.width, rec.height))
        1
      def monitors(): List[Monitor] = monitorBuffer.toList
    val callback = new MonitorEnumeratorCallback
    F.blocking {
      WinApi.api.EnumDisplayMonitors(null, null, callback, null)
      callback.monitors()
    }
  }

  override def windows(monitor: Monitor): F[List[Window]] =
    for windows <- W.windows
    yield windows.filter { window =>
      monitor.hMonitor == WinApi.api.MonitorFromWindow(window.hWND, WinUser.MONITOR_DEFAULTTONEAREST)
    }
