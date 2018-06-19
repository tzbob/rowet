package rowet.windows

import com.sun.jna.platform.win32.WTypes.LPSTR
import com.sun.jna.platform.win32.WinDef.{HWND, LPARAM}
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.win32.{StdCallLibrary, W32APIOptions}
import com.sun.jna.{Callback, Native}
import rowet.windows.Desktop32.DESKTOPENUMPROC

trait Desktop32 extends StdCallLibrary {
  def EnumDesktops(hwinsta: HANDLE,
                   lpfn: DESKTOPENUMPROC,
                   lParam: LPARAM): Boolean

  def GetParent(hWND: HWND): HWND
  def GetWindow(hWND: HWND, cmd: Int): HWND
  def IsHungAppWindow(hWND: HWND): Boolean
}

object Desktop32 {
  val INSTANCE = Native
    .loadLibrary("user32", classOf[Desktop32], W32APIOptions.DEFAULT_OPTIONS)
    .asInstanceOf[Desktop32]

  val WS_EX_TOOLWINDOW = 0x00000080L
  val WS_EX_APPWINDOW  = 0x00040000L
  val GW_OWNER         = 4

  trait DESKTOPENUMPROC extends Callback {
    def callback(lpszDesktop: LPSTR, lParam: LPARAM): Boolean
  }
}
