package com.meesam.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CartProductsTable: Table("cart_products") {
    val id = long("id").autoIncrement()
    val cartId = long("cart_id").references(UserCartTable.id, ReferenceOption.CASCADE)
    val userId = long("user_id").references(UserTable.id, ReferenceOption.CASCADE)
    val productId = long("product_id").references(ProductTable.id, ReferenceOption.CASCADE)
    val quantity = integer("quantity")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}