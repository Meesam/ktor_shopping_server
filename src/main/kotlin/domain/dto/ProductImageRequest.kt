package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductImageRequest(

    //val imagePath: MultipartFile,

    val productId: Long = 0
)
