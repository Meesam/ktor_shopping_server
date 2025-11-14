package com.meesam.domain.dto

import jakarta.validation.constraints.Positive
import kotlinx.serialization.Serializable

@Serializable
data class TogglePrimaryAddressRequest(
    @field:Positive(message = "AddressId must be greater than zero")
    val addressId: Long = 0,

    @field:Positive(message = "UserId must be greater than zero")
    val userId: Long = 0,
)
