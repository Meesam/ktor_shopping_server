package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(


    val title: String = "",


    val price: Double = 0.0,

    val description: String = "",


    val category: Long = 0,


    val quantity: Int = 0
)
