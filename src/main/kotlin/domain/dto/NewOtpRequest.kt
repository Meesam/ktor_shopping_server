package com.meesam.domain.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class NewOtpRequest(
    @field:Email(message = "email is not in correct format")
    @field:Null(message = "email cannot be null")
    @field:NotBlank(message = "email cannot be blank")
    val email: String
)
