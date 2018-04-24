package com.terefe

import com.github.tminglei.slickpg._
//
trait CustomPostgresProfile extends ExPostgresProfile
with PgDate2Support {

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
//  override protected def computeCapabilities: Set[Capability] =
//    super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API  with DateTimeImplicits


}

object CustomPostgresProfile extends CustomPostgresProfile



