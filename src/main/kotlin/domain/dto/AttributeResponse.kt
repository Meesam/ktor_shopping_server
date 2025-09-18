package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttributeResponse(
    var id: Long? = null,
    var title: String,
    var description :String? = null,
    var categoryId: Long? = null,
    var createdAt: String? = null,
    var categoryName: String
)
