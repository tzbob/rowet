package rowet.internal

trait Monitor:
  val rectangle: Rectangle
  def place(rectangle: Rectangle): Placement = Placement.on(this, rectangle)

trait MonitorCompanion[W <: Window, M <: Monitor, F[_]]:
  val monitors: F[List[M]]
  def windows(monitor: M): F[List[W]]
