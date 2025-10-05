package com.meesam.services

import com.meesam.data.repositories.AttributeRepository
import com.meesam.domain.dto.AttributeRequest
import com.meesam.domain.dto.AttributeResponse
import com.meesam.domain.dto.UpdateAttributeRequest

class AttributeService(
    private val attributeRepository: AttributeRepository = AttributeRepository()
) {
    suspend fun getAllAttribute(): List<AttributeResponse> {
        return attributeRepository.getAllAttribute()
    }

    suspend fun createAttribute(attributeRequest: AttributeRequest): Unit {
        return attributeRepository.createAttribute(attributeRequest)
    }

    suspend fun deleteAttribute(attributeId: Long): Unit {
        return attributeRepository.deleteAttribute(attributeId)
    }

    suspend fun updateAttribute(updateAttributeRequest: UpdateAttributeRequest): Unit{
        return attributeRepository.updateAttribute(updateAttributeRequest)
    }
}