package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long? = null,
    val name: String = "",
    val email: String = "",
    val dob: String? = null,
    val lastLoginAt: String? = null,
    val role: String,
    val profilePicUrl: String? = null,
)
