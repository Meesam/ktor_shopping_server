package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.AttributeTable
import com.meesam.data.tables.CategoryTable
import com.meesam.data.tables.ProductAttributesTable
import com.meesam.data.tables.ProductImagesTable
import com.meesam.data.tables.ProductTable
import com.meesam.domain.dto.ProductAttributeResponse
import com.meesam.domain.dto.ProductAttributesResponse
import com.meesam.domain.dto.ProductImagesResponse
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
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.leftJoin
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
            val attributeCount = ProductAttributesTable.productId.count()
            val productImageCount = ProductImagesTable.id.count()
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
                    CategoryTable.title,
                    CategoryTable.id,
                    attributeCount,
                    productImageCount
                )
                .where { CategoryTable.isActive eq true }
                .groupBy(ProductTable.id, ProductTable.title, CategoryTable.title, CategoryTable.id)
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
                        createdAt = it[ProductTable.createdAt],
                        productImages = it[productImageCount],
                        productAttributes = it[attributeCount]
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

    suspend fun getProductById(productId: Long): ProductResponse = dbQuery {
        try {
            val productsRows = ProductTable
                .leftJoin(CategoryTable, onColumn = { ProductTable.categoryId }, otherColumn = { CategoryTable.id })
                .leftJoin(
                    ProductImagesTable,
                    onColumn = { ProductTable.id },
                    otherColumn = { ProductImagesTable.productId })
                .leftJoin(
                    ProductAttributesTable,
                    onColumn = { ProductTable.id },
                    otherColumn = { ProductAttributesTable.productId })
                .leftJoin(
                    AttributeTable,
                    onColumn = { ProductAttributesTable.attributeId },
                    otherColumn = { AttributeTable.id })
                .select(
                    ProductTable.id,
                    ProductTable.title,
                    ProductTable.description,
                    ProductTable.price,
                    ProductTable.quantity,
                    ProductTable.categoryId,
                    ProductTable.createdAt,
                    ProductTable.isActive,
                    CategoryTable.title,
                    ProductAttributesTable.id,
                    ProductAttributesTable.productId,
                    ProductAttributesTable.attributeId,
                    AttributeTable.title,
                    ProductAttributesTable.value,
                    ProductAttributesTable.quantity,
                    ProductAttributesTable.price,
                    ProductImagesTable.imageUrl,
                    ProductImagesTable.isDefaultImage
                ).where { CategoryTable.isActive eq true and (ProductTable.id eq productId) }
                .toList()

            val productRow = productsRows.first()

            val productAttribute = productsRows
                .filter { row -> row.getOrNull(ProductAttributesTable.id) != null }
                .map { row ->
                    ProductAttributesResponse(
                        id = row[ProductAttributesTable.id],
                        productId = row[ProductAttributesTable.productId],
                        attributeId = row[ProductAttributesTable.attributeId],
                        attributeTitle = row[AttributeTable.title],
                        attributeValue = row[ProductAttributesTable.value],
                        attributeQuantity = row[ProductAttributesTable.quantity] ?: 0,
                        attributePrice = row[ProductAttributesTable.price] ?: 0.0
                    )
                }

            val productImages = productsRows
                .filter { row -> row.getOrNull(ProductImagesTable.id) != null }
                .map { row ->
                    ProductImagesResponse(
                        id = row[ProductAttributesTable.id],
                        productId = row[ProductAttributesTable.productId],
                        imageUrl = row[ProductImagesTable.imageUrl],
                        isDefaultImage = row[ProductImagesTable.isDefaultImage]
                    )
                }

            ProductResponse(
                id = productRow[ProductTable.id],
                title = productRow[ProductTable.title],
                description = productRow[ProductTable.description] ?: "No Description",
                quantity = productRow[ProductTable.quantity] ?: 0,
                price = productRow[ProductTable.price],
                isActive = productRow[ProductTable.isActive],
                categoryId = productRow[ProductTable.categoryId],
                categoryName = productRow[CategoryTable.title],
                createdAt = productRow[ProductTable.createdAt],
                productAttributesList = productAttribute,
                productImagesList = productImages
            )

        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

}