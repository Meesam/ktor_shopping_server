package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class ProductAttributeRequest(
    @field:Min(value = 1, message = "productId must be greater than zero")
    val productId: Long = 0,

    @field:Min(value = 1, message = "attributeId must be greater than zero")
    val attributeId: Long = 0,

    @field:NotNull(message = "values cannot be null")
    @field:NotBlank(message = "values cannot be blank")
    @field:Size(min = 3, max = 100, message = "values should be between 3 and 100 characters")
    val values: String,

    val price: Double? = null
)
