package rowet.windows

import cats.effect.IO
import rowet.Platform

object WinApi extends Platform[IO] {

  override type Window = rowet.windows.Window
  override type WindowCompanion = rowet.windows.Window.type
  override val Window = rowet.windows.Window

  override type Monitor = rowet.windows.Monitor
  override type MonitorCompanion = rowet.windows.Monitor.type
  override val Monitor = rowet.windows.Monitor

}
