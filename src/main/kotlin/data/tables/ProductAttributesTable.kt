package com.meesam.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProductAttributesTable: Table("product_attributes") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(ProductTable.id, ReferenceOption.RESTRICT)
    val attributeId = long("attribute_id").references(AttributeTable.id)
    val value = varchar("value", 255)
    val price = double("price").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}