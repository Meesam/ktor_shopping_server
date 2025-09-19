package com.meesam.services

import com.meesam.data.repositories.CategoryRepository
import com.meesam.domain.dto.CategoryRequest

class CategoryService(
    private val categoryRepository: CategoryRepository = CategoryRepository()
) {
    suspend fun createCategory(categoryRequest: CategoryRequest) = categoryRepository.createCategory(categoryRequest)

    suspend fun getAllCategory() = categoryRepository.getAllCategory()

    suspend fun deleteCategory(categoryId:Long) = categoryRepository.deleteCategory(categoryId)

    suspend fun updateCategory(categoryRequest: CategoryRequest) = categoryRepository.updateCategory(categoryRequest)

    suspend fun getCategoryById(categoryId: Long) = categoryRepository.getCategoryById(categoryId)
}