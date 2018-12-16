package rowet.internal

import cats.effect.Effect
import rowet.internal

abstract class Platform[F[_]: Effect] {
  type Window <: internal.Window
  type WindowCompanion <: internal.WindowCompanion[Window, F]
  val Window: WindowCompanion

  type Monitor <: internal.Monitor
  type MonitorCompanion <: internal.MonitorCompanion[Window, Monitor, F]
  val Monitor: MonitorCompanion
}
