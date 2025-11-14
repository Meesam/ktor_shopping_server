package com.meesam.domain.dto


import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class UserAddressResponse(
    val id: Long? = null,
    val city: String? = null,
    val state: String? = null,
    val pin: String? = null,
    val street: String? = null,
    val nearBy: String? = null,
    val country: String? = null,
    val userId: Long? = null,
    val contactName: String? = null,
    val isPrimary: Boolean = false,
    val contactNumber: String? = null,
    val createdAt: LocalDateTime? = null,
    val addressType: String? = null
)
