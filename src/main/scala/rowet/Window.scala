package rowet

trait Window[F[_]] {
  val title: F[String]
  val className: F[String]
}

trait WindowCompanion[F[_]] {
  val windows: F[List[Window[F]]]
}
