package com.meesam.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(

    val id: Long? = null,
    @field:NotBlank(message = "title cannot be blank")
    @field:Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
    val title: String = "",
)
