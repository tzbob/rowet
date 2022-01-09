package rowet

import cats.*
import cats.data.*
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.*
import rowet.internal.{Placement, Platform, Rectangle}

class Rowet[F[_]: Monad](val p: Platform[F]):
  import p.*

  def sideToSide[A](windows: List[A], monitor: Monitor) =
    val width = monitor.rectangle.width / windows.size
    windows.zipWithIndex.foldLeft(Map.empty[A, Placement]) { case (placements, (window, idx)) =>
      val placement = monitor.place(Rectangle(idx * width, 0, width, monitor.rectangle.height))
      placements + (window -> placement)
    }

  val test =
    def findTaiga(windows: List[Window], titles: List[String]) =
      windows.zip(titles).collect {
        case (window, title) if title.contains("Taiga") || title.contains("Steam") =>
          window
      }

    for
      allWindows <- Window.windows
      windows    <- allWindows.filterA(Window.validate)
      titles     <- windows.traverse(Window.title)
//      monitor    <- Monitor.monitors
//      taigas     = findTaiga(windows, titles)
//      placements = sideToSide(taigas, monitor(0))
//      _ <- Window.place(placements)
    yield println(s"Found ${titles.length} these: ${titles.zip(windows)}")

object Rowet extends Rowet[IO](rowet.internal.windows.Windows) with IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    test.onError(e => IO(println(e))).as(ExitCode.Success)
