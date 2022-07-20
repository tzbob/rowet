package rowet.internal.win32.ffi

import com.sun.jna.Native
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.{StdCallLibrary, W32APIOptions}
import rowet.internal.win32.ffi.DwmApi

trait DwmApi extends StdCallLibrary:
  def DwmGetWindowAttribute(hWND: HWND, dwAttr: Int, pvAttr: IntByReference, cbAttr: Int): HRESULT

object DwmApi:
  lazy val api = Native.load("dwmapi", classOf[DwmApi], W32APIOptions.DEFAULT_OPTIONS).nn

  val DWMWA_CLOAKED = 14
