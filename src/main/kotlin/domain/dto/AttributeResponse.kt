package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AttributeResponse(
    var id: Long? = null,
    var title: String,
    var description :String? = null,
    var categoryId: Long? = null,
    var createdAt: Instant,
    var categoryName: String
)
