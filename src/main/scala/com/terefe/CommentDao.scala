package com.terefe

import java.util.UUID

import slick.jdbc.meta.MTable

class CommentDao(implicit ec: scala.concurrent.ExecutionContext) {

  import CommentSchema.{CommentRow, Comments}
  import CustomPostgresProfile.api._


  def createTable: DBIOAction[Unit, NoStream, Effect.Read with Effect.Schema] =
    MTable.getTables(Comments.baseTableRow.tableName).headOption flatMap {
      case Some(_) => DBIO.successful(())
      case None    => Comments.schema.create
    }

  def create(row: CommentRow): DBIOAction[CommentRow, NoStream, Effect.Write] =
    Comments returning Comments += row

  def list(target: String): DBIOAction[List[CommentRow], NoStream, Effect.Read] =
    Comments.filter(_.target === target.trim).to[List].result

  def findAll: DBIOAction[List[CommentRow], NoStream, Effect.Read] =
    Comments.to[List].result

  def get(id: UUID): DBIOAction[Option[CommentRow], NoStream, Effect.Read] =
    Comments.filter(_.id === id).result.headOption

  def delete(id: UUID): DBIOAction[Int, NoStream, Effect.Write] =
    Comments.filter(_.id === id).delete
}
