package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.AttributeTable
import com.meesam.data.tables.ProductAttributesTable
import com.meesam.data.tables.ProductTable
import com.meesam.domain.dto.ProductAttributeRequest
import com.meesam.domain.dto.UpdateProductAttributeRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

class ProductAttributeRepository {

    suspend fun addProductAttribute(productAttributeRequest: ProductAttributeRequest): Unit = dbQuery {
        try {
            /**Check if the product exists or not**/
            val productRow = ProductTable.select(ProductTable.id, ProductTable.price, ProductTable.quantity).where {
                ProductTable.id eq productAttributeRequest.productId
            }.singleOrNull()
                ?: throw ResourceNotFoundException("Product not found")

            /**Check if the attribute exists or not**/
            AttributeTable.select(AttributeTable.id)
                .where {
                    AttributeTable.id eq productAttributeRequest.attributeId
                }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Attribute not found")

            ProductAttributesTable.insert {
                it[productId] = productAttributeRequest.productId
                it[attributeId] = productAttributeRequest.attributeId
                it[price] = productAttributeRequest.price ?: productRow[ProductTable.price]
                it[quantity] = productAttributeRequest.quantity ?: productRow[ProductTable.quantity]
                it[value] = productAttributeRequest.values
            }

        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun updateProductAttribute(updateProductAttributeRequest: UpdateProductAttributeRequest): Unit = dbQuery {
        try {

            ProductAttributesTable.select(ProductAttributesTable.id)
                .where { ProductAttributesTable.id eq updateProductAttributeRequest.id }
                .singleOrNull() ?: throw ResourceNotFoundException("Product Attribute not found")

            /**Check if the product exists or not**/
            val productRow = ProductTable.select(ProductTable.id, ProductTable.price, ProductTable.quantity).where {
                ProductTable.id eq updateProductAttributeRequest.productId
            }.singleOrNull()
                ?: throw ResourceNotFoundException("Product not found")

            /**Check if the attribute exists or not**/
            AttributeTable.select(AttributeTable.id)
                .where {
                    AttributeTable.id eq updateProductAttributeRequest.attributeId
                }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Attribute not found")

            ProductAttributesTable.update({ ProductAttributesTable.id eq updateProductAttributeRequest.id }) {
                it[productId] = updateProductAttributeRequest.productId
                it[attributeId] = updateProductAttributeRequest.attributeId
                it[price] = updateProductAttributeRequest.price ?: productRow[ProductTable.price]
                it[quantity] = updateProductAttributeRequest.quantity ?: productRow[ProductTable.quantity]
                it[value] = updateProductAttributeRequest.values
            }

        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun deleteProductAttribute(attributeId: Long): Unit = dbQuery {
        try {
            ProductAttributesTable.select(ProductAttributesTable.id).where { ProductAttributesTable.id eq attributeId }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Product Attribute not found")
            ProductAttributesTable.deleteWhere {
                ProductAttributesTable.id eq attributeId
            }
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

}