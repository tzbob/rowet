package rowet.internal

import rowet.domain.*

trait WindowAlg[W <: Window, F[_]]:
  val windows: F[List[W]]

  def validate(window: W): F[Boolean]
  def title(window: W): F[String]
  def className(window: W): F[String]

  /** Raw move command, uses absolute coordinates
    */
  def move(locations: Map[W, Rectangle]): F[Unit]

  /** Monitor specific move command, uses coordinates relative to monitors
    */
  def place(placements: Map[W, Placement]): F[Unit] =
    move(placements.view.mapValues(_.rectangle.absolute).toMap)
