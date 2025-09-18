package com.meesam.domain.dto

import kotlinx.serialization.Serializable


@Serializable
data class AddUserCartRequest(
    val title:String? = null,

    val userId:Long? = null,

    val quantity:Long = 0,

    val productId:Long? = null
)
