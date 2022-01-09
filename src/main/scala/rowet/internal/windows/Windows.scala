package rowet.internal.windows

import cats.effect.IO
import rowet.internal.Platform

object Windows extends Platform[IO]:
  override type Window          = rowet.internal.windows.Window
  override type WindowCompanion = rowet.internal.windows.Window.type

  override val Window = rowet.internal.windows.Window

  override type Monitor          = rowet.internal.windows.Monitor
  override type MonitorCompanion = rowet.internal.windows.Monitor.type

  override val Monitor = rowet.internal.windows.Monitor
