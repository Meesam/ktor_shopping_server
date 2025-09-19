package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(

    @field:NotBlank(message = "title cannot be blank")
    @field:Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
    val title: String = "",

    @field:NotNull(message = "Price cannot be null")
    @field:Positive(message = "Price must be greater than 0")
    val price: Double = 0.0,

    val description: String = "",

    @field:NotNull(message = "category cannot be null")
    @field:Min(value = 1, message = "category must be at least 1")
    @field:Positive(message = "category must be greater than zero")
    val category: Long = 0,

    @field:NotNull(message = "quantity cannot be null")
    @field:Positive(message = "quantity must be greater than zero")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int = 0
)
