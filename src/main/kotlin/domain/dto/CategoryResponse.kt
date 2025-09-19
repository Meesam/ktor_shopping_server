package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CategoryResponse(
    val id: Long? = null,
    val title:String = "",
    val createdAt: Instant
)