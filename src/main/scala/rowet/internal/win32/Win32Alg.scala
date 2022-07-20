package rowet.internal.win32

import cats.effect.{IO, Sync}
import rowet.internal.PlatformAlg

class Win32Alg[F[_]](using F: Sync[F]) extends PlatformAlg[F]:
  override type Window = rowet.internal.win32.WindowAlgWin32.Window

  override val WindowAlg = rowet.internal.win32.WindowAlgWin32()

  override type Monitor = rowet.internal.win32.MonitorAlgWin32.Monitor

  override val MonitorAlg = rowet.internal.win32.MonitorAlgWin32(WindowAlg)
