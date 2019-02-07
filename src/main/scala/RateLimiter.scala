import scala.concurrent._

import cats.effect.IO
import cats.effect.concurrent.Deferred
import upperbound.Limiter

object RateLimiter {
  implicit val ec = ExecutionContext.Implicits.global
  implicit val cs = IO.contextShift(ec)

  def apiWithSubmit[A](api: IO[A])(implicit limiter: Limiter[IO]): IO[Option[Deferred[IO, A]]] = for {
    d  <- Deferred[IO, A]
    r  <- limiter.
      submit(api.flatMap(r => d.complete(r))).
      flatMap(onSubmitSuccess(d)).
      handleErrorWith(onSubmitFailure(d))
  } yield r

  private def onSubmitSuccess[A](d: Deferred[IO, A]): Unit => IO[Option[Deferred[IO, A]]] = _ => IO {
    println(s"Submitted job successfully")
    Option(d)
  }

  private def onSubmitFailure[A](d: Deferred[IO, A]): Throwable => IO[Option[Deferred[IO, A]]] = t => IO {
    println("Error submitting job: " + t.getMessage + s" ${t.getClass.getSimpleName}")
    None
  }
}
