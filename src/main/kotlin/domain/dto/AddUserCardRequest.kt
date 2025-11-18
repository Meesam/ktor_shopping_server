package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class AddUserCardRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    val name:String,

    @field:NotNull(message = "CardNumber cannot be null")
    val cardNumber: Long,

    @field:NotNull(message = "CVV cannot be null")
    val cvv: Int,

    @field:NotNull(message = "Month cannot be null")
    val expiredMonth: Int,

    @field:NotNull(message = "Year cannot be null")
    val expiredYear: Int,

    @field:Positive(message = "UserId must be greater than zero")
    val userId:Long,
)
