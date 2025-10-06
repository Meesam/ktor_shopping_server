package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserAddressRequest(
    @field:Min(value = 1, message = "userId must be greater than zero")
    val id: Long,

    @field:NotBlank(message = "addressType cannot be blank")
    val addressType: String = "",

    @field:NotBlank(message = "address cannot be blank")
    @field:Size(min = 3, max = 100, message = "Address between 3 and 100 characters")
    val address: String = "",

    @field:NotNull(message = "city cannot be null")
    @field:NotBlank(message = "city cannot be blank")
    @field:Size(min = 3, max = 100, message = "city between 3 and 100 characters")
    val city: String = "",

    @field:NotNull(message = "state cannot be null")
    @field:NotBlank(message = "state cannot be blank")
    @field:Size(min = 3, max = 100, message = "state between 3 and 100 characters")
    val state: String = "",

    @field:NotNull(message = "country cannot be null")
    @field:NotBlank(message = "country cannot be blank")
    @field:Size(min = 3, max = 100, message = "country between 3 and 100 characters")
    val country: String = "",

    @field:NotBlank(message = "zipCode cannot be blank")
    @field:Size(min = 6, max = 6, message = "zipCode should be 6 digits")
    val zipCode: String = "",

    @field:NotNull(message = "street cannot be null")
    @field:NotBlank(message = "street cannot be blank")
    @field:Min(value = 1, message = "street must be greater than zero")
    val street:String = "",

    val comment:String? = null,

    val nearBy:String? = null,

    val isPrimary:Boolean = false,

    @field:NotNull(message = "userId cannot be null")
    @field:Min(value = 1, message = "userId must be greater than zero")
    val userId: Long = 0
)
