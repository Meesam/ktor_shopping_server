package com.meesam.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    @field:NotBlank(message = "token cannot be blank")
    @field:NotNull(message = "token cannot be null")
    val token: String
)
