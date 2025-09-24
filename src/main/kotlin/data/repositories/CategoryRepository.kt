package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.CategoryTable
import com.meesam.domain.dto.CategoryRequest
import com.meesam.domain.dto.CategoryResponse
import com.meesam.domain.exceptionhandler.ConflictException
import com.meesam.domain.exceptionhandler.DomainException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update


class CategoryRepository {

    suspend fun createCategory(categoryRequest: CategoryRequest) = dbQuery {
        try {
            CategoryTable.insert {
                it[title] = categoryRequest.title.trim()
            }
        } catch (e: ExposedSQLException) {
            if (e.sqlState == "23505") {
                throw ConflictException("Category '${categoryRequest.title}' already exists")
            }
            throw DomainException(
                message = e.message.toString()
            )
        }
    }

    suspend fun getAllCategory(): List<CategoryResponse> = dbQuery {
        try {
            CategoryTable.selectAll().where { CategoryTable.isActive eq true }.map {
                CategoryResponse(
                    id = it[CategoryTable.id],
                    title = it[CategoryTable.title],
                    createdAt = it[CategoryTable.createdAt]
                )
            }
        } catch (e: ExposedSQLException) {
            throw DomainException(
                message = e.message.toString()
            )
        }
    }

    suspend fun getCategoryById(categoryId: Long): CategoryResponse? = dbQuery {
        try {
            CategoryTable.selectAll().where { CategoryTable.id eq categoryId }.map {
                CategoryResponse(
                    id = it[CategoryTable.id],
                    title = it[CategoryTable.title],
                    createdAt = it[CategoryTable.createdAt]
                )
            }.singleOrNull()
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun deleteCategory(categoryId: Long) = dbQuery {
        try {
            val result = getCategoryById(categoryId)
            result?.let {
                CategoryTable.update({ CategoryTable.id eq categoryId }) {
                    it[isActive] = false
                }
            } ?: throw DomainException("Category not found")
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun updateCategory(categoryRequest: CategoryRequest) = dbQuery {
        try {
            categoryRequest.id?.let { id ->
                val result = getCategoryById(id)
                result?.let {
                    CategoryTable.update({ CategoryTable.id eq id }) {
                        it[title] = categoryRequest.title.trim()
                        it[isActive] = categoryRequest.isActive ?: true
                    }
                } ?: throw DomainException("Category not found")
            } ?: throw DomainException("Category Id is required")
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        }
    }
}