package com.meesam.domain.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable

//import org.springframework.web.multipart.MultipartFile

@Serializable
data class ProductImageRequest(
    @field:NotBlank(message = "imagePath cannot be blank")
    @field:NotNull(message = "productId cannot be null")
    //val imagePath: MultipartFile,

    @field:NotNull(message = "productId cannot be null")
    @field:Min(value = 1, message = "productId must be greater than zero")
    val productId: Long = 0
)
