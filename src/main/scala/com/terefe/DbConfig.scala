package com.terefe

import scala.language.higherKinds
import cats.effect.{ContextShift, Effect}

import scala.concurrent.ExecutionContext

object DbConfig {

  import slick.jdbc.JdbcBackend.Database

  val db = Database.forConfig("postgres")

  def initializeDb[F[+_]: Effect: ContextShift](implicit ec: ExecutionContext): F[Unit] = {
    Util.fromFuture(db.run(new CommentDao().createTable))
  }

}
