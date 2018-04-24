package com.terefe

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

import cats.Monad
import cats.effect.{Effect, IO}
import fs2.StreamApp

import org.http4s.rho.RhoMiddleware
import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.rho.swagger.syntax.io._
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import org.http4s.server.blaze.BlazeBuilder

object Server extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = {
    val m = createRhoMiddleware()
    ServerStream.stream[IO](ioSwagger, m)
  }
}

object ServerStream {

  def stream[F[_]: Effect: Monad](s: SwaggerSyntax[F], m: RhoMiddleware[F])(implicit ec: ExecutionContext) = {

    val commentService = new CommentServiceImpl[F](DbConfig.db, new CommentDao)

    def commentsController(s: SwaggerSyntax[F], m: RhoMiddleware[F]) =
      new CommentController[F](commentService, s).service.toService(m)

    fs2.Stream.eval(DbConfig.initializeDb) >>
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(commentsController(s, m), "/")
      .serve
  }
}
