package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.CategoryTable
import com.meesam.data.tables.ProductAttributesTable
import com.meesam.data.tables.ProductImagesTable
import com.meesam.data.tables.ProductTable
import com.meesam.domain.dto.ProductRequest
import com.meesam.domain.dto.ProductResponse
import com.meesam.domain.dto.UpdateProductRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

class ProductRepository {

    suspend fun createProduct(productRequest: ProductRequest): Unit = dbQuery {
        try {
            val categoryRow = CategoryTable
                .select(CategoryTable.title)
                .where { CategoryTable.id eq productRequest.category and (CategoryTable.isActive eq true) }
                .toList()
            if (categoryRow.isEmpty()) {
                throw ResourceNotFoundException("Category not found")
            } else {
                ProductTable.insert {
                    it[title] = productRequest.title.trim()
                    it[categoryId] = productRequest.category
                    it[description] = productRequest.description.trim()
                    it[price] = productRequest.price
                    it[quantity] = productRequest.quantity
                }
            }
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun getAllProduct(): List<ProductResponse> = dbQuery {
        try {
            ProductTable
                .leftJoin(CategoryTable)
                .leftJoin(ProductImagesTable)
                .leftJoin(ProductAttributesTable)
                .select(
                    ProductTable.id,
                    ProductTable.title,
                    ProductTable.description,
                    ProductTable.price,
                    ProductTable.quantity,
                    ProductTable.categoryId,
                    ProductTable.createdAt,
                    ProductTable.isActive,
                    CategoryTable.title
                )
                .where { CategoryTable.isActive eq true }
                .map {
                    ProductResponse(
                        id = it[ProductTable.id],
                        title = it[ProductTable.title],
                        description = it[ProductTable.description] ?: "No Description",
                        quantity = it[ProductTable.quantity] ?: 0,
                        price = it[ProductTable.price],
                        isActive = it[ProductTable.isActive],
                        categoryId = it[ProductTable.categoryId],
                        categoryName = it[CategoryTable.title],
                        createdAt = it[ProductTable.createdAt]
                    )
                }.toList()
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun deleteProduct(productId: Long): Unit = dbQuery {
        try {
            ProductTable.select(ProductTable.id).where { ProductTable.id eq productId }.singleOrNull()
                ?: throw ResourceNotFoundException("Product not found")

            /**Delete Images and Attributes of the product**/
            ProductImagesTable.deleteWhere { ProductImagesTable.productId eq productId }
            ProductAttributesTable.deleteWhere { ProductAttributesTable.productId eq productId }
            ProductTable.deleteWhere { ProductTable.id eq productId }
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun updateProduct(updateProductRequest: UpdateProductRequest): Unit = dbQuery {
        try {
            val updateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            ProductTable.select(ProductTable.id).where { ProductTable.id eq updateProductRequest.productId }
                .singleOrNull()
                ?: throw ResourceNotFoundException("Product not found")

            ProductTable.update({ ProductTable.id eq updateProductRequest.productId }) {
                it[title] = updateProductRequest.title.trim()
                it[description] = updateProductRequest.description.trim()
                it[price] = updateProductRequest.price
                it[quantity] = updateProductRequest.quantity
                it[updatedAt] = updateTime
            }

        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun getAllProductById(productId: Long): ProductResponse = dbQuery {
        try {
            ProductTable
                .leftJoin(CategoryTable)
                .leftJoin(ProductImagesTable)
                .leftJoin(ProductAttributesTable)
                .select(
                    ProductTable.id,
                    ProductTable.title,
                    ProductTable.description,
                    ProductTable.price,
                    ProductTable.quantity,
                    ProductTable.categoryId,
                    ProductTable.createdAt,
                    ProductTable.isActive,
                    CategoryTable.title
                )
                .where { CategoryTable.isActive eq true and (CategoryTable.id eq productId) }
                .map {
                    ProductResponse(
                        id = it[ProductTable.id],
                        title = it[ProductTable.title],
                        description = it[ProductTable.description] ?: "No Description",
                        quantity = it[ProductTable.quantity] ?: 0,
                        price = it[ProductTable.price],
                        isActive = it[ProductTable.isActive],
                        categoryId = it[ProductTable.categoryId],
                        categoryName = it[CategoryTable.title],
                        createdAt = it[ProductTable.createdAt]
                    )
                }.singleOrNull() ?: throw ResourceNotFoundException("Product not found")
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

}