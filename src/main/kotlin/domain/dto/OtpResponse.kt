package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class OtpResponse(
    val otpSent: Boolean,
    val otp: Int,
    val email:String
)
