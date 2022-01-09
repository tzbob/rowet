package rowet.internal.windows

import cats.effect.IO
import com.sun.jna.platform.win32.BaseTSD.{DWORD_PTR, LONG_PTR}
import com.sun.jna.platform.win32.WinDef.{DWORD, HWND, PVOID, RECT}
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.platform.win32.{User32, WinUser}
import com.sun.jna.ptr.IntByReference
import com.sun.jna.{Native, Pointer}
import rowet.internal
import rowet.internal.windows.DwmApi.*
import rowet.internal.windows.DwmApi.api.*
import rowet.internal.windows.WinApi.*
import rowet.internal.windows.WinApi.api.*
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
      EnumWindows(windowCallback, null)
      windowCallback.windows()
    }
  }

  /** Follow the implementation of PowerToys FancyZones
    * @param w
    * @return
    */
  override def validate(w: Window): IO[Boolean] = IO.delay {
    val hWND = w.hWND

    val isAncestor = GetAncestor(hWND, GA_ROOT) == hWND
    val visible    = IsWindowVisible(hWND)

    val validStyle =
      val style = GetWindowLong(hWND, WinUser.GWL_STYLE)
      val isUninterestingPopup = (style & WS_POPUP) == WS_POPUP &&
        (style & WS_THICKFRAME) == 0 &&
        (style & WS_MINIMIZEBOX) == 0 &&
        (style & WS_MAXIMIZEBOX) == 0

      val isChild    = (style & WS_CHILD) == WS_CHILD
      val isDisabled = (style & WS_DISABLED) == WS_DISABLED
      !isUninterestingPopup && !isChild && !isDisabled

    val validExStyle =
      val exStyle      = GetWindowLong(hWND, WinUser.GWL_EXSTYLE)
      val isToolWindow = (exStyle & WS_EX_TOOLWINDOW) == WS_EX_TOOLWINDOW
      val isNoActivate = (exStyle & WS_EX_NOACTIVATE) == WS_EX_NOACTIVATE
      !isToolWindow && !isNoActivate

    val isSystem =
      val systemHwnds = Set(GetDesktopWindow(), GetShellWindow())
      val systemClasses = Set(
        "SysListView32",
        "WorkerW",
        "Shell_TrayWnd",
        "Shell_SecondaryTrayWnd",
        "Progman",
        "Windows.UI.Core.CoreWindow"
      )
      val className =
        val chars = new Array[Char](1024)
        GetClassName(hWND, chars, chars.length)
        Native.toString(chars)
      systemHwnds.contains(hWND) || systemClasses.contains(className)

    val hasVisibleOwner =
      val owner    = GetWindow(hWND, GW_OWNER)
      val hasOwner = owner != null
      hasOwner && IsWindowVisible(owner) && {
        val rect = RECT()
        GetWindowRect(owner, rect) && rect.top != rect.bottom && rect.left == rect.right
      }

    val isCloaked =
      val cloakVal = IntByReference()
      val hres     = DwmGetWindowAttribute(hWND, DWMWA_CLOAKED, cloakVal, 4)
      hres.longValue == 0 && cloakVal.getValue != 0

    !isCloaked && isAncestor && visible && validStyle && validStyle && validExStyle && !isSystem && !hasVisibleOwner
  }

  override def title(window: Window): IO[String] =
    IO.delay {
      val chars = new Array[Char](1024)
      GetWindowText(window.hWND, chars, chars.length)
      Native.toString(chars)
    }

  override def className(window: Window): IO[String] = {
    val chars = new Array[Char](1024)
    IO.delay {
      GetClassName(window.hWND, chars, chars.length)
      Native.toString(chars)
    }
  }

  /** Raw move command, uses absolute coordinates
    */
  override def move(locations: Map[Window, Rectangle]): IO[Unit] = IO.blocking {
    val hDWP = BeginDeferWindowPos(locations.size)

    def deferWindow(w: Window, rectangle: Rectangle, posInfo: HDWP): HDWP =
      DeferWindowPos(
        posInfo,
        w.hWND,
        null,
        rectangle.x,
        rectangle.y,
        rectangle.width,
        rectangle.height,
        SWP_NOZORDER
      )

    val finalHDWP = locations.foldLeft(hDWP) { case (hDWPNext, (w, g)) =>
      deferWindow(w, g, hDWPNext)
    }

    EndDeferWindowPos(finalHDWP)
  }
