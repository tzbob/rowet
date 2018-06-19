package rowet

trait Platform[F[_]] {
  type Window <: rowet.Window[F]
  type WindowCompanion <: rowet.WindowCompanion[Window, F]
  val Window: WindowCompanion

  type Monitor <: rowet.Monitor[F]
  type MonitorCompanion <: rowet.MonitorCompanion[F]
  val Monitor: MonitorCompanion
}
