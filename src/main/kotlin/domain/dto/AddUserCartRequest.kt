package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class AddUserCartRequest(
    val title:String? = null,

    @field:Positive(message = "userId must be greater than zero")
    val userId:Long? = null,

    @field:NotNull(message = "quantity cannot be null")
    @field:Positive(message = "quantity must be greater than zero")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity:Long = 0,

    @field:Positive(message = "productId must be greater than zero")
    val productId:Long? = null
)
