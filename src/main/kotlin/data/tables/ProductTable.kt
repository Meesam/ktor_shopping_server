package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProductTable: Table("products") {
    val id = long("id").autoIncrement()
    val title = varchar("title", 50)
    val description = varchar("description", 500).nullable()
    val price = double("price")
    val quantity = integer("quantity").nullable()
    val categoryId = long("category_id").references(CategoryTable.id)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()
    override val primaryKey = PrimaryKey(id)
}