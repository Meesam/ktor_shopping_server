package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlinx.datetime.Instant
import com.meesam.data.tables.UserTable


object RefreshTokensTable : Table("refresh_tokens") {
    val id = long("id").autoIncrement()
    val tokenHash = varchar("token_hash", 128).index()
    val userId = long("user_id").references(UserTable.id).index()
    val jti = varchar("jti", 64).index()
    val expiresAt = timestamp("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val revokedAt = timestamp("revoked_at").nullable()
    val replacedByJti = varchar("replaced_by_jti", 64).nullable()
    override val primaryKey = PrimaryKey(id)
}
