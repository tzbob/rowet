package rowet

trait Platform[F[_]] {
  type Window <: rowet.Window
  type WindowCompanion <: rowet.WindowCompanion[Window, F]
  val Window: WindowCompanion

  type Monitor <: rowet.Monitor
  type MonitorCompanion <: rowet.MonitorCompanion[Window, Monitor, F]
  val Monitor: MonitorCompanion
}
