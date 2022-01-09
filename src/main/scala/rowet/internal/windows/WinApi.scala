package rowet.internal.windows

import com.sun.jna.platform.win32.User32

object WinApi:
  val desktop32: User32Ext = User32Ext.INSTANCE.nn
  val user32: User32       = User32.INSTANCE.nn

  export desktop32.*
  export user32.*
