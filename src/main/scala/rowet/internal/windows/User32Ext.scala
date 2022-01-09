package rowet.internal.windows

import com.sun.jna.platform.win32.WTypes.LPSTR
import com.sun.jna.platform.win32.WinDef.{HWND, LPARAM, UINT}
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.win32.{StdCallLibrary, W32APIOptions}
import com.sun.jna.{Callback, Native}

import com.sun.jna.platform.win32.{User32, WinUser}

trait User32Ext extends StdCallLibrary:
  val WS_EX_TOOLWINDOW = 0x00000080L
  val WS_EX_APPWINDOW  = 0x00040000L
  val GW_OWNER         = 4
  val SWP_NOZORDER     = new UINT(0x0004)

  type HDWP = HWND

  def EnumDesktops(hwinsta: HANDLE, lpfn: DESKTOPENUMPROC, lParam: LPARAM): Boolean

  def GetWindow(hWND: HWND, cmd: Int): HWND
  def IsHungAppWindow(hWND: HWND): Boolean
  def BeginDeferWindowPos(nNumWindows: Int): HDWP

  /** https://docs.microsoft.com/en-us/windows/desktop/api/winuser/nf-winuser-deferwindowpos
    */
  def DeferWindowPos(
      hWinPosInfo: HDWP,
      hWnd: HWND,
      hWndInsertAfter: HWND,
      x: Int,
      y: Int,
      cx: Int,
      cy: Int,
      uFlags: UINT
  ): HDWP

  def EndDeferWindowPos(hWinPosInfo: HDWP): Boolean

  trait DESKTOPENUMPROC extends Callback:
    def callback(lpszDesktop: LPSTR, lParam: LPARAM): Boolean

object User32Ext:
  lazy val INSTANCE: User32Ext = Native
    .load("user32", classOf[User32Ext], W32APIOptions.DEFAULT_OPTIONS)
    .nn
