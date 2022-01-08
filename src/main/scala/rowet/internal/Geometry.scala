package rowet.internal

case class Geometry(x: Int, y: Int, width: Int, height: Int):
  case class SubGeometry(g: Geometry):
    val parent: Geometry   = Geometry.this
    val absolute: Geometry = g.copy(x = g.x + parent.x, y = g.y + parent.y)
