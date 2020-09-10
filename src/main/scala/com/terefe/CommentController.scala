package com.terefe

import cats.effect.Effect
import cats.implicits._
import cats.Monad
import CommentSchema.CommentRow
import fs2.Stream
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.time.Instant
import java.util.UUID
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.rho._
import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.{Entity, EntityEncoder, MediaType}
import org.slf4j.LoggerFactory
import scala.collection.compat.immutable.ArraySeq
import scala.language.higherKinds

class CommentController[F[+_]: Effect: Monad](commentService: CommentService[F], swaggerSyntax: SwaggerSyntax[F])
    extends Http4sDsl[F] {
  import swaggerSyntax._

  val log = LoggerFactory.getLogger(getClass)

  val service: RhoRoutes[F] = new RhoRoutes[F] {

    "Get all comments " **
      GET / "comments" |>> { () =>
      for {
        xs <- commentService.findAll
        res <- Ok(xs)
      } yield res
    }

    "Get comments for target" **
      GET / "comments" / 'target / "list" |>> { target: String =>
      for {
        xs <- commentService.list(target)
        res <- Ok(xs)
      } yield res
    }

    "Get comments by uuid" **
      GET / "comments" / pathVar[UUID] |>> { id: UUID =>
      for {
        o <- commentService.get(id)
        res <- Ok(o)
      } yield res
    }

    "Delete comments by uuid" **
      DELETE / "comments" / pathVar[UUID] |>> { id: UUID =>
      for {
        o <- commentService.delete(id)
        res <- Ok(o.asJson)
      } yield res
    }

    "Post new comment" **
      POST / "comments" ^ postCommentFmt |>> { data: PostComment =>
      for {
        createResult <- commentService.create(data)
        res <- Created(createResult)
      } yield res
    }
  }
}

case class PostComment(
    target: String,
    text: String,
    parent: Option[UUID]
)
