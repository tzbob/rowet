package rowet.windows

import cats.effect.IO
import com.sun.jna.{Native, Pointer}
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import rowet.WindowCompanion

import scala.collection.mutable.ListBuffer

case class Window(private[windows] val hWND: HWND) extends rowet.Window[IO] {

  override val title: IO[String] = IO {
    val chars = new Array[Char](1024)
    User32.INSTANCE.GetWindowText(hWND, chars, chars.length)
    Native.toString(chars)
  }

  override val className: IO[String] = IO {
    val chars = new Array[Char](1024)
    User32.INSTANCE.GetClassName(hWND, chars, chars.length)
    Native.toString(chars)
  }

}

object Window extends WindowCompanion[IO] {

  override val windows: IO[List[Window]] = IO {
    val windowCallback = new WindowEnumeratorCallback
    User32.INSTANCE.EnumWindows(windowCallback, null)
    windowCallback.windows()
  }

  class WindowEnumeratorCallback extends WNDENUMPROC {
    private[this] val windowHandlers = ListBuffer.empty[Window]

    override def callback(hWnd: HWND, data: Pointer): Boolean = {
      windowHandlers += new Window(hWnd)
      true
    }

    def windows(): List[Window] = windowHandlers.toList
  }
}
