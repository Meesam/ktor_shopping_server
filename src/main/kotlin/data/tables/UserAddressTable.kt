package com.meesam.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserAddressTable : Table("user_addresses") {

    val id = long("id").autoIncrement()
    val addressType = varchar("address_type", 50)
    val street = varchar("street", 255)
    val city = varchar("city", 255)
    val state = varchar("state", 255)
    val country = varchar("country", 150)
    val zipCode = varchar("zip_code", 50)
    val nearBy = varchar("nearBy", 255).nullable()
    val comment = varchar("comment", 255).nullable()
    val isPrimary = bool("is_primary").default(false)
    val userId = long("user_id").references(UserTable.id, onDelete = ReferenceOption.CASCADE).index()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)

}