package rowet.internal

trait Monitor {
  val geometry: Geometry
  def place(geometry: Geometry): Placement = Placement.on(this, geometry)
}

trait MonitorCompanion[W <: Window, M <: Monitor, F[_]] {
  val monitors: F[List[M]]
  def windows(monitor: M): F[List[W]]
}
