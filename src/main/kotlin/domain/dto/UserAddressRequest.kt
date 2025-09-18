package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserAddressRequest(

    val address: String = "",

    val city: String = "",

    val state: String = "",

    val country: String = "",

    val pin: String = "",

    val street:String = "",

    val nearBy:String? = null,

    val userId: Long = 0
)
