package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.AttributeTable
import com.meesam.data.tables.CategoryTable
import com.meesam.domain.dto.AttributeRequest
import com.meesam.domain.dto.AttributeResponse
import com.meesam.domain.dto.UpdateAttributeRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

class AttributeRepository {
    suspend fun getAllAttribute(): List<AttributeResponse> = dbQuery {
        try {
            AttributeTable.innerJoin(CategoryTable)
                .select(
                    AttributeTable.id,
                    AttributeTable.title,
                    AttributeTable.categoryId,
                    AttributeTable.createdAt,
                    CategoryTable.title
                ).where {
                    CategoryTable.isActive eq true
                }.map {
                    AttributeResponse(
                        id = it[AttributeTable.id],
                        title = it[AttributeTable.title],
                        categoryId = it[AttributeTable.categoryId],
                        createdAt = it[AttributeTable.createdAt],
                        categoryName = it[CategoryTable.title]
                    )
                }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun createAttribute(attributeRequest: AttributeRequest): Unit = dbQuery {
        try {
            val categoryRow = CategoryTable
                .select(CategoryTable.title)
                .where { CategoryTable.id eq attributeRequest.categoryId and (CategoryTable.isActive eq true) }
                .toList()
            if (categoryRow.isEmpty()) {
                throw ResourceNotFoundException("Category not found")
            } else {
                AttributeTable.insert {
                    it[title] = attributeRequest.title.trim()
                    it[categoryId] = attributeRequest.categoryId
                }
            }

        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        }catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        }
        catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun deleteAttribute(attributeId: Long): Unit = dbQuery {
        try {
            AttributeTable.select(AttributeTable.title).where { AttributeTable.id eq attributeId }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Attribute not found")
            AttributeTable.deleteWhere {
                AttributeTable.id eq attributeId
            }
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun updateAttribute(updateAttributeRequest: UpdateAttributeRequest): Unit = dbQuery {
        try {
            AttributeTable.select(AttributeTable.title).where { AttributeTable.id eq updateAttributeRequest.id }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Attribute not found")

            val categoryRow = CategoryTable
                .select(CategoryTable.title)
                .where { CategoryTable.id eq updateAttributeRequest.categoryId and (CategoryTable.isActive eq true) }
                .toList()
            if (categoryRow.isEmpty()) {
                throw ResourceNotFoundException("Category not found")
            } else {
                AttributeTable.update({ AttributeTable.id eq updateAttributeRequest.id }) {
                    it[title] = updateAttributeRequest.title
                    it[categoryId] = updateAttributeRequest.categoryId
                }
            }
        } catch (e: ResourceNotFoundException) {
            throw NotFoundException(e.message.toString())
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }
}