import upperbound._
import upperbound.syntax.rate._

import cats.implicits._
import cats.effect._
import cats.effect.concurrent._

import scala.concurrent._
import scala.concurrent.duration._

import RateLimiter.apiWithSubmit

object Example extends App {

  implicit val ec = ExecutionContext.Implicits.global
  implicit val cs = IO.contextShift(ec)
  implicit val tr = IO.timer(ec)

  def calculateSomethingDifficult(n: Int): IO[Int] = IO(n * 2)

  def readOut(n: Int, deferred: Deferred[IO, Int]): IO[Unit] = deferred.
    get.
    timeout(5.seconds).
    flatMap(r => IO(println(s"RESULT of $n = ${r}"))).
    handleErrorWith(_ => IO(println(s"TIMEOUT of $n")))

  val program: IO[List[Unit]] = Limiter.start[IO](10 every 1.second, 10).use {
    limiter =>
      implicit val l = limiter
      for {
        ns <- IO((0 to 99).toList)
        ds <- ns.map(n => apiWithSubmit(calculateSomethingDifficult(n))).parSequence
        rs <- ns.zip(ds).filter(_._2.nonEmpty).map {
          case (n, Some(d)) => readOut(n, d)
        }.parSequence
      } yield rs
  }

  program.unsafeRunSync()
}