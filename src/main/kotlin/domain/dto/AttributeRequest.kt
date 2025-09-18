package com.meesam.domain.dto

import kotlinx.serialization.Serializable


@Serializable
data class AttributeRequest(
    val title: String,

    val description :String? = null,


    val categoryId: Long? = null

)
