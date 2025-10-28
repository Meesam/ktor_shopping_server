package com.meesam.services

import com.meesam.data.repositories.UserRepository
import com.meesam.domain.dto.DeleteProfilePictureRequest
import com.meesam.domain.dto.UpdateUserAddressRequest
import com.meesam.domain.dto.UserAddressRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import com.meesam.services.firebase.FirebaseStorageService
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.nio.file.Paths
import kotlin.io.path.copyTo
import kotlin.io.path.deleteIfExists

class UserService(
    private val userRepository: UserRepository = UserRepository(),
    private val firebaseStorageService: FirebaseStorageService = FirebaseStorageService()
) {
    suspend fun updateUserDetails(
        userUpdateRequest: UserUpdateRequest,
        fileName: String?,
    ): UserResponse? {
        try {
            val userExists = userRepository.isUserExists(userUpdateRequest.id)
            if (userExists) {
                fileName?.let {
                } ?: return userRepository.updateUserDetails(userUpdateRequest)
            } else {
                throw ResourceNotFoundException("User not found")
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
        return null
    }

    suspend fun addUserProfileImage(
        userUpdateRequest: UserUpdateRequest,
        imageUrl: String?,
    ): UserResponse? {
        try {
            val userExists = userRepository.isUserExists(userUpdateRequest.id)
            if (userExists) {
                imageUrl?.let {
                    try {
                        val payload = userUpdateRequest.copy(profilePicUrl = imageUrl)
                        return userRepository.updateUserDetails(payload)
                    }catch (e: Exception){
                        throw DomainException(e.message.toString())
                    }
                }
            } else {
                throw ResourceNotFoundException("User not found")
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
        return null
    }

    suspend fun deleteProfilePicture(deleteProfilePictureRequest: DeleteProfilePictureRequest): UserResponse?{
        try {
            val userExists = userRepository.isUserExists(deleteProfilePictureRequest.id)
            if (userExists) {
                deleteProfilePictureRequest.profilePicUrl?.let {
                    val result = firebaseStorageService.deleteFile(it)
                    if (result) {
                        return userRepository.deleteProfilePicture(deleteProfilePictureRequest.id)
                    }
                } ?: throw ResourceNotFoundException("User Image not found")
            } else {
                throw ResourceNotFoundException("User not found")
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
        return null
    }

    suspend fun addUserAddress(userAddressRequest: UserAddressRequest){
        return userRepository.addUserAddress(userAddressRequest)
    }

    suspend fun deleteUserAddress(addressId: Long){
        return userRepository.deleteUserAddress(addressId)
    }

    suspend fun updateUserAddress(userAddressRequest: UpdateUserAddressRequest){
        return userRepository.updateUserAddress(userAddressRequest)
    }
}