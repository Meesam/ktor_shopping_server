package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long? = null,
    val title: String = "",
    val createdAt: kotlinx.datetime.LocalDateTime
)