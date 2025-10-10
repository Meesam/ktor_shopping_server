package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductAttributesResponse(
    val id: Long,
    val attributeTitle: String,
    val attributeValue: String,
    val attributePrice: Double? = null,
    val productId: Long,
    val attributeId: Long,
    val attributeQuantity: Int? = null,
)
