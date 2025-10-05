package com.meesam.domain.dto

//import com.meesam.springshopping.model.ProductAttributes
//import com.meesam.springshopping.model.ProductImages
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long? = null,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val categoryId: Long? = null,
    val categoryName: String = "",
    val quantity: Int = 0,
    val createdAt: kotlinx.datetime.LocalDateTime,
    val isActive: Boolean,
    //val productImages: List<ProductImagesResponse> = emptyList(),
    //val productAttributes: List<ProductAttributesResponse> = emptyList()
)
