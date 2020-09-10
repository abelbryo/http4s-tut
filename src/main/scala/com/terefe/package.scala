package com

import java.time.Instant
import java.util.UUID
import scala.collection.compat.immutable.ArraySeq

import cats.effect.Sync
import io.circe.Json
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.{EntityEncoder, EntityDecoder}

package object terefe {
  import CommentSchema.CommentRow

  implicit val encodeUUID: Encoder[UUID] = (a: UUID) => Json.fromString(a.toString)
  implicit val decodeUUID: Decoder[UUID] = _.as[String].map(UUID.fromString)
  implicit val encodeInstant: Encoder[Instant] = (a: Instant) => Json.fromString(a.toString)
  implicit val decodeInstant: Decoder[Instant] = _.as[String].map(Instant.parse)
  implicit def commentJsonFmt[F[_]: Sync](implicit dec: Decoder[CommentRow]) = jsonOf[F, CommentRow]
  implicit def postCommentFmt[F[_]: Sync](implicit dec: Decoder[PostComment]) = jsonOf[F, PostComment]

  implicit def commentRowEntityEncoder[F[_]](implicit enc: Encoder[CommentRow]) = jsonEncoderOf[F, CommentRow]
  implicit def optCommentRowEntityEncoder[F[_]](implicit enc: Encoder[CommentRow]) = jsonEncoderOf[F, Option[CommentRow]]
  implicit def listCommentRowEntityEncoder[F[_]](implicit enc: Encoder[CommentRow]) = jsonEncoderOf[F, List[CommentRow]]
}
