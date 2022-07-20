package rowet.domain

trait Monitor:
  val rectangle: Rectangle
  def place(rectangle: Rectangle): Placement = Placement.on(this, rectangle)
