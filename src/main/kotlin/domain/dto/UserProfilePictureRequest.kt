package com.meesam.domain.dto

import kotlinx.serialization.Serializable

//import org.springframework.web.multipart.MultipartFile

@Serializable
data class UserProfilePictureRequest(
   // val profilePicUrl: MultipartFile,
    val userId: Long = 0
)
