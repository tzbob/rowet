package rowet.internal.windows

import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.platform.win32.{User32, WinUser}
import com.sun.jna.{Native, Pointer}

import scala.collection.mutable.ListBuffer

class WindowTester extends App {
  val win32          = User32.INSTANCE
  val windowCallback = new EnumWindowCallback

  win32.EnumWindows(windowCallback, null)
}

class EnumWindowCallback extends WNDENUMPROC {
  private[this] val windowHandlers = ListBuffer.empty[HWND]

  override def callback(hWnd: HWND, data: Pointer): Boolean = {
    if (!isValidWindow(hWnd)) return true

    windowHandlers += hWnd

    val chars = new Array[Char](1024)
    User32.INSTANCE.GetWindowText(hWnd, chars, chars.length)
    val windowText = Native.toString(chars)

    println(windowText)

    val className = {
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetClassName(hWnd, chars, chars.length)
      Native.toString(chars)
    }

    println(className)
    true
  }

  def windows(): List[HWND] = windowHandlers.toList

  def isValidWindow(hWND: HWND): Boolean = {
    val visible = User32.INSTANCE.IsWindowVisible(hWND)

    val windowText = {
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetWindowText(hWND, chars, chars.length)
      Native.toString(chars)
    }

    val hasTitle     = windowText != ""
    val isRootWindow = Desktop32.INSTANCE.GetParent(hWND) == null

    val extendedWindowStyles =
      User32.INSTANCE.GetWindowLong(hWND, WinUser.GWL_EXSTYLE)

    val className = {
      val chars = new Array[Char](1024)
      User32.INSTANCE.GetClassName(hWND, chars, chars.length)
      Native.toString(chars)
    }

    val isNativeWindowsFrame = (windowText, className) match {
      case ("Settings", "Windows.UI.Core.CoreWindow")        => true
      case ("Settings", "ApplicationFrameWindow")            => true
      case ("Microsoft Store", "Windows.UI.Core.CoreWindow") => true
      case ("Microsoft Store", "ApplicationFrameWindow")     => true
      case ("Program Manager", "Progman")                    => true
      case _                                                 => false
    }

    val isToolWindow = (extendedWindowStyles & Desktop32.WS_EX_TOOLWINDOW) == 1
    val isAppWindow  = (extendedWindowStyles & Desktop32.WS_EX_APPWINDOW) == 1

    val hasOwner = Desktop32.INSTANCE
      .GetWindow(hWND, Desktop32.GW_OWNER) != null

    visible && hasTitle && isRootWindow && !isToolWindow && !isAppWindow && !hasOwner && !isNativeWindowsFrame
  }
}
