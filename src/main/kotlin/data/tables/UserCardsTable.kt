package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserCardsTable: Table("user_cards") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)
    val cardNumber = long("cardNumber").uniqueIndex("uq_user_card_number")
    val cvv = integer("cvv")
    val expiredMonth = integer("expired_Month")
    val expiredYear = integer("expired_Year")
    val userId = long("user_id").references(UserTable.id)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}