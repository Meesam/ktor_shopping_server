package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class UserUpdateRequest(
    val id: Long = 0,
    val name: String= "",
    val dob: Instant? = null,
    val profilePicUrl: String? = null
)
