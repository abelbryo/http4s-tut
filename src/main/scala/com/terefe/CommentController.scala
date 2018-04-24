package com.terefe

import java.util.UUID
import java.time.Instant

import scala.language.higherKinds

import cats.effect.Effect
import cats.Monad
import cats.implicits._

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.rho._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.slf4j.LoggerFactory
import CommentSchema.CommentRow

class CommentController[F[_]: Effect: Monad](commentService: CommentService[F],
                                             swaggerSyntax: SwaggerSyntax[F]) extends Http4sDsl[F] {
  import swaggerSyntax._

  val log = LoggerFactory.getLogger(getClass)

  implicit val encodeUUID: Encoder[UUID] = (a: UUID) => Json.fromString(a.toString)
  implicit val decodeUUID: Decoder[UUID] = _.as[String] map UUID.fromString
  implicit val encodeInstant: Encoder[Instant] = (a: Instant) => Json.fromString(a.toString)
  implicit val decodeInstant: Decoder[Instant] = _.as[String] map Instant.parse
  implicit val commentJsonFmt: EntityDecoder[F, CommentRow] = jsonOf[F, CommentRow]
  implicit val postCommentFmt: EntityDecoder[F, PostComment] = jsonOf[F, PostComment]


  val service: RhoService[F] = new RhoService[F] {

       "Get all comments " **
       GET / "comments" |>> { ()  =>
         Ok(commentService.findAll.map(_.asJson))
       }

        "Get comments for target" **
       GET / "comments" / 'target / "list" |>> { target: String =>
         Ok(commentService.list(target) map (_.asJson))
       }

      "Get comments by uuid" **
       GET /  "comments" / pathVar[UUID] |>> { id: UUID =>
         Ok(commentService.get(id) map (_.asJson))
       }

      "Delete comments by uuid" **
       DELETE / "comments" / pathVar[UUID] |>> { id: UUID =>
         Ok(commentService.delete(id) map (_.asJson))
       }

      "Post new comment" **
       POST /  "comments" ^ postCommentFmt |>> { data: PostComment  =>
         Created(commentService.create(data).map(_.asJson))
       }
    }

}

case class PostComment(
  target: String,
  text:   String,
  parent: Option[UUID]
)

