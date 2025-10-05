package com.meesam.domain.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AttributeResponse(
    var id: Long? = null,
    var title: String,
    var categoryId: Long? = null,
    var createdAt: LocalDateTime,
    var categoryName: String
)
