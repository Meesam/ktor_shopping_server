package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime

@Serializable
data class UserRequest(
    val name :String = "",
    val email: String = "" ,
    val password: String = "",
    val role:String? = null,
    val dob: Instant? = null,
    val lastLoginAt: String? = null,
    val createdAt: Instant? = null
)
