package com.terefe

import scala.language.higherKinds
import cats.effect.Effect

object DbConfig {

  import slick.jdbc.JdbcBackend.Database

  val db = Database.forConfig("postgres")

  def initializeDb[F[_]: Effect](implicit ex: scala.concurrent.ExecutionContext):F[Unit] ={
    Util.fromFuture(db.run(new CommentDao().createTable))
  }

}



