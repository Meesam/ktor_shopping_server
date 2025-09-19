package com.meesam.domain.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val timestamp: Instant = Clock.System.now(),
    val details: Map<String, String>? = null
)

