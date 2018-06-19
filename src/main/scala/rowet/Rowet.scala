package rowet

import cats.FlatMap
import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._

class Rowet[F[_]: Monad](p: Platform[F]) {
  import p._

//  Monitor.monitors.map { monitors =>
//    monitors.map { monitor =>
//      monitor.windows.map(ws => monitor -> ws)
//    }
//  }

  val printAllWindows = for {
    monitors          <- Monitor.monitors
    monitorWindowList <- monitors.map(m => m.windows.map(_ -> m)).sequence
  } yield {
    for {
      (windows, monitor) <- monitorWindowList
    } {
      println(s"$monitor has $windows")
    }
  }

  val printAllWindowsEz = for {
    windows    <- Window.windows
    classNames <- windows.map(_.className).sequence
  } yield {
    println(classNames.mkString("\nClassName:"))
//    println(s"ClassName: $className, Title: $title")
  }

}

object Rowet extends Rowet[IO](rowet.windows.WinApi) {
  def main(args: Array[String]): Unit = {
    printAllWindows.unsafeRunSync()
  }
}
