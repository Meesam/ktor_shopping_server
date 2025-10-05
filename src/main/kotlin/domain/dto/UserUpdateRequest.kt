package com.meesam.domain.dto

import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable


@Serializable
data class UserUpdateRequest(
    @field:Positive(message = "userId must be greater than zero")
    val id: Long = 0,
    val name: String?= "",
    val dob: kotlinx.datetime.LocalDate? = null,
    val profilePicUrl: String? = null
)
