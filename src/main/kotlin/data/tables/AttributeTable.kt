package com.meesam.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime


object AttributeTable: Table("attributes") {
    val id = long("id").autoIncrement()
    val title = varchar("title", 50)
    val categoryId = long("category_id").references(CategoryTable.id)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}