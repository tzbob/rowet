package rowet

trait Monitor

trait MonitorCompanion[W <: Window, M <: Monitor, F[_]] {
  val monitors: F[List[M]]

  val windows: F[M => List[W]]
  val geometry: F[M => Geometry]
}
