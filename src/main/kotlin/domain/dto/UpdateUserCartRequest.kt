package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserCartRequest(
    val id:Long,

    @field:NotNull(message = "quantity cannot be null")
    @field:Positive(message = "quantity must be greater than zero")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val userId: Long,

    @field:Positive(message = "productId must be greater than zero")
    val productId:Long
)
