package com.meesam.domain.dto

import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class NewOtpRequest(
    @field:Positive(message = "userId must be greater than zero")
    val userId: Long
)
