package rowet

trait Window[F[_]] {
  val title: F[String]
  val className: F[String]
}

trait WindowCompanion[W <: Window[F], F[_]] {
  val windows: F[List[W]]
  val validate: F[W => Boolean]
}
