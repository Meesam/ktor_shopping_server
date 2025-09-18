package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val title: String = "",
)
