package com.meesam.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductImagesResponse(
   val id:Long,
   val imageUrl:String?=null,
   val productId:Long,
   val isDefaultImage:Boolean
)
