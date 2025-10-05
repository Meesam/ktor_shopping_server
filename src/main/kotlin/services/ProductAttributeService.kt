package com.meesam.services

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.repositories.ProductAttributeRepository
import com.meesam.domain.dto.ProductAttributeRequest
import com.meesam.domain.dto.UpdateProductAttributeRequest

class ProductAttributeService(
    private val productAttributeRepository: ProductAttributeRepository = ProductAttributeRepository()
) {

    suspend fun addProductAttribute(productAttributeRequest: ProductAttributeRequest): Unit {
        return productAttributeRepository.addProductAttribute(productAttributeRequest)
    }

    suspend fun updateProductAttribute(updateProductAttributeRequest: UpdateProductAttributeRequest): Unit{
        return productAttributeRepository.updateProductAttribute(updateProductAttributeRequest)
    }

    suspend fun deleteProductAttribute(attributeId: Long): Unit{
        return productAttributeRepository.deleteProductAttribute(attributeId)
    }
}