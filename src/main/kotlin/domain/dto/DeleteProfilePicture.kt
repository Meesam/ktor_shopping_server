package com.meesam.domain.dto

import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class DeleteProfilePictureRequest(
    @field:Positive(message = "userId must be greater than zero")
    val id: Long = 0,
    val profilePicUrl: String? = null
)
