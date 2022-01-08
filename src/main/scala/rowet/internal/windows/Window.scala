package rowet.internal.windows

import cats.effect.IO
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.platform.win32.{User32, WinUser}
import com.sun.jna.{Native, Pointer}
import rowet.internal
import rowet.internal.windows.Desktop32.HDWP
import rowet.internal.{Geometry, Placement, WindowCompanion}

import scala.collection.mutable.ListBuffer
import scala.language.unsafeNulls

case class Window(private[windows] val hWND: HWND) extends rowet.internal.Window

object Window extends WindowCompanion[Window, IO]:
  override val windows: IO[List[Window]] = IO {
    class WindowEnumeratorCallback extends WNDENUMPROC:
      private[this] val windowHandlers = ListBuffer.empty[Window]

      override def callback(hWnd: HWND, data: Pointer): Boolean =
        windowHandlers += new Window(hWnd)
        true

      def windows(): List[Window] = windowHandlers.toList

    val windowCallback = new WindowEnumeratorCallback
    User32.INSTANCE.EnumWindows(windowCallback, null)
    windowCallback.windows()
  }

  override def validate(w: Window): IO[Boolean] = IO {
    val hWND = w.hWND

    val visible      = User32.INSTANCE.IsWindowVisible(hWND)
    val isRootWindow = Desktop32.INSTANCE.GetParent(hWND) == null

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

    val isToolWindow = (extendedWindowStyles & Desktop32.WS_EX_TOOLWINDOW) == 1
    val hasOwner = Desktop32.INSTANCE
      .GetWindow(hWND, Desktop32.GW_OWNER) != null

    val isAppWindow = (extendedWindowStyles & Desktop32.WS_EX_APPWINDOW) == 1

    (visible && isRootWindow) && ((!isToolWindow && !hasOwner) || isAppWindow && hasOwner) && !isNativeWindowsFrame && hasTitle
  }

  override def title(window: Window): IO[String] =
    IO {
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetWindowText(window.hWND, chars, chars.length)
      Native.toString(chars)
    }

  override def className(window: Window): IO[String] = IO {
    val chars = new Array[Char](1024)
    User32.INSTANCE.GetClassName(window.hWND, chars, chars.length)
    Native.toString(chars)
  }

  /** Raw move command, uses absolute coordinates
    */
  override def move(locations: Map[Window, Geometry]): IO[Unit] = IO {
    val hDWP = Desktop32.INSTANCE.BeginDeferWindowPos(locations.size)

    def deferWindow(w: Window, geometry: Geometry, posInfo: HDWP): HDWP =
      Desktop32.INSTANCE.DeferWindowPos(
        posInfo,
        w.hWND,
        null,
        geometry.x,
        geometry.y,
        geometry.width,
        geometry.height,
        Desktop32.SWP_NOZORDER
      )

    val finalHDWP = locations.foldLeft(hDWP) { case (hDWPNext, (w, g)) =>
      deferWindow(w, g, hDWPNext)
    }

    Desktop32.INSTANCE.EndDeferWindowPos(finalHDWP)
  }
