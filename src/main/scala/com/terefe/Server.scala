package com.terefe

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

import cats.Monad
import cats.effect.{Effect, IO}
import cats.implicits._

import org.http4s.rho.RhoMiddleware
import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.rho.swagger.syntax.io._
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import org.http4s.server.blaze.BlazeBuilder
import cats.effect.IOApp
import cats.effect.ContextShift
import cats.effect.ConcurrentEffect
import cats.effect.Timer
import cats.effect.ExitCode

object Server extends IOApp {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String]) = {
    val m = createRhoMiddleware()
    ServerStream.stream[IO](ioSwagger, m)
  }

  def run(args: List[String]): IO[ExitCode] = {
    stream(args).compile.drain.as(ExitCode.Success)
  }
}

object ServerStream {
  implicit val ec = scala.concurrent.ExecutionContext.global

  implicit val cs = IO.contextShift(ec)

  def stream[F[+_]: Effect: ContextShift: ConcurrentEffect: Timer](s: SwaggerSyntax[F], m: RhoMiddleware[F]) = {

    val commentService = new CommentServiceImpl[F](DbConfig.db, new CommentDao)

    def commentsController(s: SwaggerSyntax[F], m: RhoMiddleware[F]) =
      new CommentController[F](commentService, s).service.toRoutes(m)

    fs2.Stream.eval(DbConfig.initializeDb) >>
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(commentsController(s, m), "/")
      .serve
  }
}
