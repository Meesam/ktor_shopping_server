package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable


@Serializable
data class DeleteProductFileRequest(
    @field:Min(value = 1, message = "productId must be greater than zero")
    val productImageId: Long = 0,

    @field:NotBlank(message = "image cannot be blank")
    val image: String = "",
)
