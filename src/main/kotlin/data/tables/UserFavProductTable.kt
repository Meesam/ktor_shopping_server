package com.meesam.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserFavProductTable: Table("user_fav_products") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id, ReferenceOption.CASCADE)
    val productId = long("product_id").references(ProductTable.id, ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}