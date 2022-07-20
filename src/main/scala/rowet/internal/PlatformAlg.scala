package rowet.internal

import rowet.internal

abstract class PlatformAlg[F[_]]:
  type Window <: rowet.domain.Window
  val WindowAlg: internal.WindowAlg[Window, F]

  type Monitor <: rowet.domain.Monitor
  val MonitorAlg: internal.MonitorAlg[Window, Monitor, F]
