package rowet

import cats._
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import rowet.internal.{Rectangle, Placement, Platform}

class Rowet[F[_]: Sync](val p: Platform[F]):
  import p._

  def sideToSide[A](windows: List[A], monitor: Monitor) =
    val width = monitor.rectangle.width / windows.size
    windows.zipWithIndex.foldLeft(Map.empty[A, Placement]) { case (placements, (window, idx)) =>
      val placement = monitor.place(Rectangle(idx * width, 0, width, monitor.rectangle.height))
      placements + (window -> placement)
    }

  val test =
    def findTaiga(windows: List[p.Window], titles: List[String]) =
      windows.zip(titles).collect {
        case (window, title) if title.contains("Taiga") || title.contains("Steam") =>
          window
      }

    for
      allWindows <- Window.windows
      windows    <- allWindows.filterA(Window.validate)
      titles     <- windows.traverse(Window.title)
      monitor    <- Monitor.monitors
      taigas     = findTaiga(windows, titles)
      placements = sideToSide(taigas, monitor(0))
      _ <- Window.place(placements)
    yield println(s"Moved $taigas on $placements")

object Rowet extends Rowet[IO](rowet.internal.windows.WinApi) with IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    test.onError(e => IO(println(e))).as(ExitCode.Success)
