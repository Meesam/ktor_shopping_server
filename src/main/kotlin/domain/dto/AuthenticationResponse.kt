package com.meesam.domain.dto

import kotlinx.serialization.Serializable


@Serializable
data class AuthenticationResponse(
    val token: String,
    val refreshToken: String? = null,
    val user:UserResponse? = null
)
