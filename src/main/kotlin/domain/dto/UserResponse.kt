package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long? = null,
    val name: String = "",
    val email: String = "",
    val dob: Instant? = null,
    val lastLoginAt: Instant? = null,
    val role: String,
    val profilePicUrl: String? = null,
    val otp: Int? = null
)
