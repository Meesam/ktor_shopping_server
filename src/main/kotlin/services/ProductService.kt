package com.meesam.services

import com.meesam.data.repositories.ProductRepository
import com.meesam.domain.dto.ProductRequest
import com.meesam.domain.dto.ProductResponse
import com.meesam.domain.dto.UpdateProductRequest

class ProductService(
    private val productRepository: ProductRepository = ProductRepository()
) {

    suspend fun createProduct(productRequest: ProductRequest): Unit{
        return productRepository.createProduct(productRequest)
    }

    suspend fun getAllProduct(): List<ProductResponse>{
        return productRepository.getAllProduct()
    }

    suspend fun deleteProduct(productId: Long): Unit{
        return productRepository.deleteProduct(productId)
    }

    suspend fun updateProduct(updateProductRequest: UpdateProductRequest): Unit{
        return productRepository.updateProduct(updateProductRequest)
    }
    suspend fun getProductById(productId: Long): ProductResponse {
        return productRepository.getProductById(productId)
    }
}