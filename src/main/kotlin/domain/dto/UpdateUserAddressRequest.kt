package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserAddressRequest(
    @field:Min(value = 1, message = "AddressId must be greater than zero")
    val id: Long,

    @field:NotBlank(message = "addressType cannot be blank")
    val addressType: String = "",

    val city: String? = null,

    val state: String? = null,

    val country: String? = null,

    val zipCode: String? = null,

    @field:NotNull(message = "street cannot be null")
    @field:NotBlank(message = "street cannot be blank")
    @field:Min(value = 1, message = "street must be greater than zero")
    val street:String = "",

    val comment:String? = null,

    val nearBy:String? = null,

    val isPrimary:Boolean = false,

    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 3, max = 100, message = "Name should be between 3 and 100 characters")
    val contactName: String = "",

    @field:NotBlank(message = "Contact number cannot be blank")
    @field:Size(min = 10, max = 15, message = "Contact number should be 10 digits")
    val contactNumber: String = "",

    @field:NotNull(message = "userId cannot be null")
    @field:Min(value = 1, message = "userId must be greater than zero")
    val userId: Long = 0
)
