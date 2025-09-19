package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class UserAddressUpdateRequest(

    @field:NotNull(message = "id cannot be null")
    @field:Min(value = 1, message = "id must be greater than zero")
    val id: Long = 0,

    @field:NotNull(message = "address cannot be null")
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

    @field:NotNull(message = "address cannot be null")
    @field:NotBlank(message = "address cannot be blank")
    @field:Size(min = 1, max = 10, message = "pin between 1 and 10 characters")
    val pin: String = "",

    @field:NotNull(message = "street cannot be null")
    @field:NotBlank(message = "street cannot be blank")
    @field:Min(value = 1, message = "street must be greater than zero")
    val street:String = "",

    val nearBy:String? = null,

    @field:NotNull(message = "userId cannot be null")
    @field:Min(value = 1, message = "userId must be greater than zero")
    val userId: Long = 0
)
