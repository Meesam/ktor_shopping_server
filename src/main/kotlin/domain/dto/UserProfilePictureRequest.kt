package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfilePictureRequest(
    //val profilePicUrl: MultipartFile,
    val userId: Long = 0
)
