package com.meesam.services

import com.meesam.data.repositories.ProductImageRepository
import com.meesam.domain.dto.DeleteProductFileRequest
import com.meesam.domain.dto.ProductImageRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import com.meesam.services.firebase.FirebaseStorageService
import org.jetbrains.exposed.exceptions.ExposedSQLException

class ProductImagesService(
    private val firebaseStorageService: FirebaseStorageService = FirebaseStorageService(),
    private val productImageRepository: ProductImageRepository = ProductImageRepository()
) {

    suspend fun uploadProductImage(
        productId: Long?,
        imageBytes: ByteArray?,
        fileName: String?,
        contentType: String?
    ): Unit {
        val result = firebaseStorageService.uploadFile(imageBytes, fileName, contentType)
        if (result.isNotEmpty()) {
            val productImageRequest = ProductImageRequest(
                productId = productId,
                imagePath = result
            )
            productImageRepository.addNewProductImage(productImageRequest)
        }
    }

    suspend fun deleteProductFile(deleteProductFileRequest: DeleteProductFileRequest) {
        try {
            val productImage =
                productImageRepository.isDeletingProductImageIsExist(deleteProductFileRequest.productImageId)
            if (productImage) {
                val result = firebaseStorageService.deleteFile(deleteProductFileRequest.image)
                if (result) {
                    return productImageRepository.deleteProductImage(deleteProductFileRequest)
                }
            } else {
                throw ResourceNotFoundException("Product Image not found")
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }
}