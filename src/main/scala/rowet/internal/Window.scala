package rowet.internal

trait Window

trait WindowCompanion[W <: Window, F[_]] {
  val windows: F[List[W]]

  def validate(window: W): F[Boolean]
  def title(window: W): F[String]
  def className(window: W): F[String]

  /**
    * Raw move command, uses absolute coordinates
    */
  def move(locations: Map[W, Geometry]): F[Unit]

  /**
    * Monitor specific move command, uses coordinates relative to monitors
    */
  def place(placements: Map[W, Placement]): F[Unit] =
    move(placements.view.mapValues(_.geometry.absolute).toMap)
}
