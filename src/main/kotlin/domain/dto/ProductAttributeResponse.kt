package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductAttributeResponse(
    val id: Long?,
    val productId: Long,
    val attributeId: Long,
    val attributeTitle: String,
    val values: String?,
    val price: Double?,
    val createdAt: kotlinx.datetime.LocalDateTime
)

