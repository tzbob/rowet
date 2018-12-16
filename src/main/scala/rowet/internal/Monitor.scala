package rowet.internal

trait Monitor {
  val geometry: Geometry
}

trait MonitorCompanion[W <: Window, M <: Monitor, F[_]] {
  val monitors: F[List[M]]
  val windows: F[M => List[W]]
}
