package com.meesam.domain.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(
    @field:NotBlank(message = "email cannot be blank")
    @field:Email(message = "invalid email address")
    val email: String,

    @field:NotBlank(message = "password cannot be blank")
    val password: String
)
