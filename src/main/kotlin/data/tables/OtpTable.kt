package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OtpTable : Table("otp_table") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val email = varchar("email", 100).references(UserTable.email).index()
    val otp = integer("otp").check { it greaterEq 100000 and (it lessEq 999999) }
    val expiresAt = timestamp("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}