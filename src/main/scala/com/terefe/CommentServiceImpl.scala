package com.terefe

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext
import scala.language.higherKinds
import scala.util.{Success, Failure}

import cats.Monad
import cats.implicits._

import cats.effect.{Effect, IO, LiftIO}
import com.terefe.CommentSchema.CommentRow
import slick.jdbc.JdbcBackend.DatabaseDef
import cats.effect.ContextShift

trait CommentService[F[_]] {
  def create(r: PostComment): F[CommentRow]
  def list(target: String): F[List[CommentRow]]
  def get(id: UUID): F[Option[CommentRow]]
  def delete(id: UUID): F[Int]
  def findAll: F[List[CommentRow]]
}

class CommentServiceImpl[F[+_]: Effect: ContextShift](db: DatabaseDef, dao: CommentDao) extends CommentService[F] {

  def create(r: PostComment): F[CommentRow] = {
    val action = dao.create(CommentRow.toRow(r.target, r.parent, r.text))
    Util.fromFuture(db.run(action))
  }

  def findAll: F[List[CommentRow]] =
    Util.fromFuture(db.run(dao.findAll))

  def list(target: String): F[List[CommentRow]] =
    Util.fromFuture(db.run(dao.list(target)))

  def get(id: UUID): F[Option[CommentRow]] =
    Util.fromFuture(db.run(dao.get(id)))

  def delete(id: UUID): F[Int] =
      Util.fromFuture(db.run(dao.delete(id)))

}

object Util {

  def fromFuture[F[+_]: ContextShift, A](f: => Future[A])(implicit F: Effect[F]): F[A] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    F.delay(f) >>= (
      f => F.async { cb =>
        f.onComplete {
          case Success(a)  => cb(Right(a))
          case Failure(ex) => cb(Left(ex))
        }
      }
      )
  }

}

