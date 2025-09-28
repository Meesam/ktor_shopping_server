package com.meesam.domain.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class ActivateUserByOtpRequest(
    @field:Positive(message = "Otp must be greater than zero")
    @field:Min(100000)
    @field:Max(999999)
    val otp: Int,

    @field:NotBlank(message = "email cannot be blank")
    @field:Email(message = "invalid email address")
    val email: String
)
