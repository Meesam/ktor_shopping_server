package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String? = null
)
