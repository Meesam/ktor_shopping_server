package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserAddressResponse(
    val id: Long? = null,
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pin: String = "",
    val street:String = "",
    val nearBy:String? = null,
    val country:String? = null,
    val userId: Long? = null,
    val userName:String = "",
    val createdAt: Instant
)
