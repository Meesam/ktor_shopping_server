package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val id: Long = 0,
    val name: String= "",
    val dob: String? = null,
    val profilePicUrl: String? = null
)
