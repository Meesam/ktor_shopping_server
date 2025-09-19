package com.meesam.domain.dto

//import com.meesam.springshopping.model.ProductAttributes
//import com.meesam.springshopping.model.ProductImages
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ProductResponse(
    val id: Long? = null,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val categoryId: Long? = null,
    val categoryName: String = "",
    val quantity: Int = 0,
    val createdAt: Instant,
    //val productImages: List<ProductImages> = emptyList(),
    //val productAttributes: List<ProductAttributes> = emptyList()
)
