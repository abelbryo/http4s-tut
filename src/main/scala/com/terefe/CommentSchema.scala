package com.terefe

import java.time.Instant
import java.util.UUID
import org.http4s.circe._

object CommentSchema {

  case class CommentRow(
      id: UUID,
      target: String,
      parentId: Option[UUID],
      text: String,
      createdAt: Instant,
      isDeleted: Boolean
  )

  object CommentRow {

    def toRow(target: String, parentId: Option[UUID], text: String) =
      new CommentRow(UUID.randomUUID(), target.trim, parentId, text, Instant.now, false)

    val tupled = (CommentRow.apply _).tupled
  }

  import CustomPostgresProfile.api._

  val Comments = TableQuery[CommentTable]
  class CommentTable(tag: Tag) extends Table[CommentRow](tag, "comments") {
    def id = column[UUID]("id", O.PrimaryKey)
    def target = column[String]("target")
    def parentId = column[Option[UUID]]("parent_id")
    def text = column[String]("text")
    def createdAt = column[Instant]("created_at")
    def isDeleted = column[Boolean]("is_deleted")

    def parentIdFkey =
      foreignKey("comments_parent_id_fkey", parentId, Comments)(
        _.id,
        onDelete = ForeignKeyAction.Cascade,
        onUpdate = ForeignKeyAction.Cascade
      )

    def targetIdx = index("comments_target_idx", target, unique = false)

    def * = (id, target, parentId, text, createdAt, isDeleted).mapTo[CommentRow]
  }

}
