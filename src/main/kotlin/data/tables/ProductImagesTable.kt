package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProductImagesTable: Table("product_images") {
    val id = long("id").autoIncrement()
    val imageUrl = varchar("image_url", 500)
    val productId = long("product_id").references(ProductTable.id)
    val color = varchar("color", 100).nullable()
    val isDefaultImage = bool("is_default_image").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}