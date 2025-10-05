package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAttributeRequest(
    @field:Min(value = 1, message = "attribute id must be greater than zero")
    @field:Positive(message = "attribute id must be greater than zero")
    var id: Long,

    @field:NotBlank(message = "title cannot be blank")
    @field:Size(min = 3, max = 50, message = "title cannot be blank")
    var title: String,

    @field:Min(value = 1, message = "categoryId must be greater than zero")
    @field:Positive(message = "categoryId must be greater than zero")
    var categoryId: Long
)
