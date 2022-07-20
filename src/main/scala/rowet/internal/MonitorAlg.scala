package rowet.internal

import rowet.domain.*

trait MonitorAlg[W <: Window, M <: Monitor, F[_]]:
  val monitors: F[List[M]]
  def windows(monitor: M): F[List[W]]
