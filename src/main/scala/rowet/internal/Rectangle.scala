package rowet.internal

case class Rectangle(x: Int, y: Int, width: Int, height: Int):
  case class SubRectangle(g: Rectangle):
    val parent: Rectangle   = Rectangle.this
    val absolute: Rectangle = g.copy(x = g.x + parent.x, y = g.y + parent.y)
