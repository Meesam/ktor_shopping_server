package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long? = null,
    val name: String = "",
    val email: String = "",
    val dob: kotlinx.datetime.LocalDate? = null,
    val lastLoginAt: kotlinx.datetime.LocalDateTime? = null,
    val role: String,
    val profilePicUrl: String? = null,
    val otp: Int? = null
)
