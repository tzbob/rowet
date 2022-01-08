package rowet.internal

trait Placement:
  val monitor: Monitor
  val rectangle: monitor.rectangle.SubRectangle

  override def toString: String = rectangle.absolute.toString

object Placement:
  def on(m: Monitor, g: Rectangle): Placement = new Placement {
    override val monitor: Monitor = m
    override val rectangle: monitor.rectangle.SubRectangle =
      monitor.rectangle.SubRectangle(g)
  }
