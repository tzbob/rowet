package rowet.internal

trait Placement {
  val monitor: Monitor
  val geometry: monitor.geometry.SubGeometry
}

object Placement {
  def apply(m: Monitor)(g: Geometry): Placement = new Placement {
    override val monitor: Monitor = m
    override val geometry: monitor.geometry.SubGeometry =
      monitor.geometry.SubGeometry(g)
  }
}
