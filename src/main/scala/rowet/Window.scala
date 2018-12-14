package rowet

trait Window

trait WindowCompanion[W <: Window, F[_]] {
  val windows: F[List[W]]
  val validate: F[W => Boolean]
  val move: F[Map[W, Geometry] => Unit]
  val title: F[W => String]
  val className: F[W => String]
}
