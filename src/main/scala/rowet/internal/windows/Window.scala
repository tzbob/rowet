package rowet.internal.windows

import cats.effect.IO
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.platform.win32.{User32, WinUser}
import com.sun.jna.{Native, Pointer}
import rowet.internal
import rowet.internal.windows.WinApi.desktop32.HDWP
import rowet.internal.{Placement, Rectangle, WindowCompanion}

import scala.collection.mutable.ListBuffer
import scala.language.unsafeNulls

case class Window(private[windows] val hWND: HWND) extends rowet.internal.Window

object Window extends WindowCompanion[Window, IO]:
  override val windows: IO[List[Window]] = {
    class WindowEnumeratorCallback extends WNDENUMPROC:
      private[this] val windowHandlers = ListBuffer.empty[Window]

      override def callback(hWnd: HWND, data: Pointer): Boolean =
        windowHandlers += new Window(hWnd)
        true

      def windows(): List[Window] = windowHandlers.toList

    val windowCallback = new WindowEnumeratorCallback
    IO.blocking {
      User32.INSTANCE.EnumWindows(windowCallback, null)
      windowCallback.windows()
    }
  }

  override def validate(w: Window): IO[Boolean] = IO.delay {
    val hWND = w.hWND

    val visible      = WinApi.IsWindowVisible(hWND)
    val isRootWindow = WinApi.GetParent(hWND) == null

    val extendedWindowStyles =
      User32.INSTANCE.GetWindowLong(hWND, WinUser.GWL_EXSTYLE)

    val className =
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetClassName(hWND, chars, chars.length)
      Native.toString(chars)

    val windowText =
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetWindowText(hWND, chars, chars.length)
      Native.toString(chars)

    val hasTitle = windowText != ""

    val isNativeWindowsFrame = (windowText, className) match
      case ("Settings", "Windows.UI.Core.CoreWindow")        => true
      case ("Settings", "ApplicationFrameWindow")            => true
      case ("Microsoft Store", "Windows.UI.Core.CoreWindow") => true
      case ("Microsoft Store", "ApplicationFrameWindow")     => true
      case ("Program Manager", "Progman")                    => true
      case _                                                 => false

    val isToolWindow = (extendedWindowStyles & WinApi.WS_EX_TOOLWINDOW) == 1
    val hasOwner     = WinApi.GetWindow(hWND, WinApi.GW_OWNER) != null

    val isAppWindow = (extendedWindowStyles & WinApi.WS_EX_APPWINDOW) == 1

    (visible && isRootWindow) && ((!isToolWindow && !hasOwner) || isAppWindow && hasOwner) && !isNativeWindowsFrame && hasTitle
  }

  override def title(window: Window): IO[String] =
    IO.delay {
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetWindowText(window.hWND, chars, chars.length)
      Native.toString(chars)
    }

  override def className(window: Window): IO[String] = {
    val chars = new Array[Char](1024)
    IO.delay {
      User32.INSTANCE.GetClassName(window.hWND, chars, chars.length)
      Native.toString(chars)
    }
  }

  /** Raw move command, uses absolute coordinates
    */
  override def move(locations: Map[Window, Rectangle]): IO[Unit] = IO.blocking {
    val hDWP = WinApi.BeginDeferWindowPos(locations.size)

    def deferWindow(w: Window, rectangle: Rectangle, posInfo: HDWP): HDWP =
      WinApi.DeferWindowPos(
        posInfo,
        w.hWND,
        null,
        rectangle.x,
        rectangle.y,
        rectangle.width,
        rectangle.height,
        WinApi.SWP_NOZORDER
      )

    val finalHDWP = locations.foldLeft(hDWP) { case (hDWPNext, (w, g)) =>
      deferWindow(w, g, hDWPNext)
    }

    WinApi.EndDeferWindowPos(finalHDWP)
  }
