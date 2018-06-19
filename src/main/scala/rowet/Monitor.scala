package rowet

trait Monitor[F[_]] {
  val windows: F[List[Window[F]]]
  val geometry: F[Geometry]
}

trait MonitorCompanion[F[_]] {
  val monitors: F[List[Monitor[F]]]
}