package rowet.internal

trait Placement:
  val monitor: Monitor
  val geometry: monitor.geometry.SubGeometry

  override def toString: String = geometry.absolute.toString

object Placement:
  def on(m: Monitor, g: Geometry): Placement = new Placement {
    override val monitor: Monitor = m
    override val geometry: monitor.geometry.SubGeometry =
      monitor.geometry.SubGeometry(g)
  }
