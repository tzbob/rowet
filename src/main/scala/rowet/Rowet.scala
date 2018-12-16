package rowet

import cats._
import cats.effect.IO
import cats.implicits._
import rowet.internal.{Geometry, Platform}

class Rowet[F[_]: Monad](p: Platform[F]) {
  import p._

//  val printMons = (Monitor.monitors, Monitor.geometry).mapN { (monitors, g) =>
//    val gs = monitors.map(g)
//    gs.foreach(println)
//  }

//  val printAllWindowsEz = for {
//    windows    <- Window.windows
//    classNames <- windows.map(_.className).sequence
//  } yield {
//    println(classNames.mkString("\nClassName:"))
//    println(s"ClassName: $className, Title: $title")
//  }

  val test = {
    val targetWs = (Window.windows , Window.validate, Window.title).mapN { (ws, p, t) =>
      val validWs = ws.filter(p)
      val result = validWs.filter(w => t(w).contains("Bitt") || t(w).contains("Spot"))
      println(result.map(t))
      result
    }

    (targetWs, Window.move).mapN { (ws, move) =>
      println(ws)
      val movements = Map (
        ws(0) -> Geometry(0, 0, 500, 500),
        ws(1) -> Geometry(600, 600, 500, 500)
      )

      move(movements)
    }
  }

}

object Rowet extends Rowet[IO](rowet.internal.windows.WinApi) {
  def main(args: Array[String]): Unit = {
    test.unsafeRunSync()
  }
}
