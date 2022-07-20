package rowet

import cats.*
import cats.data.*
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.*
import rowet.domain.{Placement, Rectangle}
import rowet.internal.PlatformAlg

class Rowet[F[_]: Monad](val p: PlatformAlg[F]):
  import p.*

  def sideToSide[A](windows: List[A], monitor: Monitor) =
    val width = monitor.rectangle.width / windows.size
    windows.zipWithIndex.foldLeft(Map.empty[A, Placement]) { case (placements, (window, idx)) =>
      val placement = monitor.place(Rectangle(idx * width, 0, width, monitor.rectangle.height))
      placements + (window -> placement)
    }

  val test =
    def findTestWindows(windows: List[Window], titles: List[String]) =
      windows.zip(titles).collect {
        case (window, title) if List("Taiga", "Paint", "Plex").exists(title.contains) =>
          window
      }

    for
      allWindows <- WindowAlg.windows
      windows    <- allWindows.filterA(WindowAlg.validate)
      titles     <- windows.traverse(WindowAlg.title)
      classNames <- windows.traverse(WindowAlg.className)
      monitor    <- MonitorAlg.monitors
      testWindows = findTestWindows(windows, titles)
      placements  = sideToSide(testWindows, monitor(1))
      _ <- WindowAlg.place(placements)
    yield
      println(s"Found ${titles.length} these: ${titles.zip(classNames)}")
      println(s"Found ${monitor.length} these: ${monitor}")
      println(s"Placed ${testWindows} with ${placements.values.toList}")

object Rowet extends Rowet[IO](rowet.internal.win32.Win32Alg[IO]) with IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    test.onError(e => IO(println(e))).as(ExitCode.Success)
