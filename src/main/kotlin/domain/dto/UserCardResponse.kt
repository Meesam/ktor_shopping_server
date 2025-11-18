package com.meesam.domain.dto

import kotlinx.serialization.Serializable
import org.threeten.bp.Year

@Serializable
data class UserCardResponse(
    val name:String,
    val cardNumber: Long,
    val expiryMonth: Int,
    val expiryYear: Int,
    val isActive:Boolean,
    val cvv:Int
)
