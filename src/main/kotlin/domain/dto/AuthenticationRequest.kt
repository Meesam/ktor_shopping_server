package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(

    val email: String,

    val password: String
)
