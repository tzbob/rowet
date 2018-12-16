package rowet.internal

trait Window

trait WindowCompanion[W <: Window, F[_]] {
  val windows: F[List[W]]

  val validate: F[W => Boolean]
  val title: F[W => String]
  val className: F[W => String]

  /**
  * Raw move command, uses absolute coordinates
    */
  val move: F[Map[W, Geometry] => Unit]

  /**
  * Monitor specific move command, uses coordinates relative to monitors
    */
  val place: F[Map[W, Placement] => Unit]
}
