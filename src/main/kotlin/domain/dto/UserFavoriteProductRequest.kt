package com.meesam.domain.dto

import kotlinx.serialization.Serializable

data class UserFavoriteProductRequest(

    val productId: Long = 0,

    val userId: Long = 0
)
