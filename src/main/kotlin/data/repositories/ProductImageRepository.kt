package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.ProductImagesTable
import com.meesam.data.tables.ProductTable
import com.meesam.domain.dto.DeleteProductFileRequest
import com.meesam.domain.dto.ProductImageRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class ProductImageRepository {

    suspend fun addNewProductImage(productImageRequest: ProductImageRequest): Unit = dbQuery {
        try {
            productImageRequest.productId?.let {
                ProductTable.select(ProductTable.id).where {
                    ProductTable.id eq productImageRequest.productId
                }.singleOrNull() ?: throw ResourceNotFoundException("Product not found")

                ProductImagesTable.insert {
                    it[imageUrl] = productImageRequest.imagePath
                    it[productId] = productImageRequest.productId
                    it[isDefaultImage] = productImageRequest.isDefaultImage ?: false
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

    suspend fun deleteProductImage(deleteProductFileRequest: DeleteProductFileRequest): Unit = dbQuery {
        try {
            ProductImagesTable.deleteWhere {
                ProductImagesTable.id eq deleteProductFileRequest.productImageId
            }
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: ExposedSQLException) {
            throw DomainException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun isDeletingProductImageIsExist(id: Long): Boolean = dbQuery {
        try {
            ProductImagesTable.selectAll().where {
                ProductImagesTable.id eq id
            }.singleOrNull()
                ?.let { true } ?: false
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

}