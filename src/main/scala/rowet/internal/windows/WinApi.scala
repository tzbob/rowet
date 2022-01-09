package rowet.internal.windows

import com.sun.jna.platform.win32.BaseTSD.{DWORD_PTR, LONG_PTR}
import com.sun.jna.platform.win32.WTypes.LPSTR
import com.sun.jna.platform.win32.WinDef.{DWORD, HWND, LPARAM, PVOID, UINT}
import com.sun.jna.platform.win32.WinNT.{HANDLE, HRESULT}
import com.sun.jna.win32.{StdCallLibrary, W32APIOptions}
import com.sun.jna.{Callback, Native}
import com.sun.jna.platform.win32.{User32, WinUser}

trait WinApi extends User32:
  type HDWP = HWND

  def EnumDesktops(hwinsta: HANDLE, lpfn: DESKTOPENUMPROC, lParam: LPARAM): Boolean

  def GetShellWindow(): HWND
  def GetWindow(hWND: HWND, cmd: Int): HWND
  def IsHungAppWindow(hWND: HWND): Boolean
  def BeginDeferWindowPos(nNumWindows: Int): HDWP

  /** https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getancestor
    */
  def GetAncestor(hWND: HWND, gaFlags: UINT): HWND

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

object WinApi:
  lazy val api = Native.load("user32", classOf[WinApi], W32APIOptions.DEFAULT_OPTIONS).nn

  val WS_EX_TOOLWINDOW = 0x00000080L
  val WS_EX_APPWINDOW  = 0x00040000L
  val WS_EX_NOACTIVATE = 0x08000000L
  val WS_CHILD         = 0x40000000L
  val WS_DISABLED      = 0x08000000L
  val WS_POPUP         = 0x80000000L
  val WS_THICKFRAME    = 0x00040000L
  val WS_MINIMIZEBOX   = 0x00020000L
  val WS_MAXIMIZEBOX   = 0x00010000L

  val GW_OWNER = 4

  val SWP_NOZORDER = new UINT(0x0004L)
  val GA_ROOT      = new UINT(2L)
