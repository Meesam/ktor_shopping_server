package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductAttributeRequest(

    val productId: Long = 0,

    val attributeId: Long = 0,

    val values: String,

    val price: Double? = null
)
