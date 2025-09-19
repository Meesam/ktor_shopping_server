package com.meesam.domain.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class ProductAttributeResponse(
    val id: Long?,
    val productId: Long,
    val attributeId: Long,
    val attributeTitle: String,
    val values: String?,
    val price: Double?,
    val createdAt: Instant
)

