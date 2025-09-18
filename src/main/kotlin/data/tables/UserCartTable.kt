package com.meesam.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserCartTable: Table("user_cart") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id, ReferenceOption.CASCADE)
    val title = varchar("title", 100).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}