package com.meesam.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : Table("users") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex("uq_users_email")
    val phone = varchar("phone", 15).nullable()
    val dob = date("dob").nullable()
    val lastLoginAt = datetime("last_login_at").nullable()
    val profilePicUrl = varchar("profile_pic_url", 255).nullable()
    val password = varchar("password", 255)
    val role = varchar("role", 20)
    val isActive = bool("is_active").default(true).nullable()
    val isActivatedByOtp = bool("is_activated_by_otp").default(false).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}