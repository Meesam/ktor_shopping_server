package com.meesam.services

import com.meesam.data.repositories.UserCartRepository
import com.meesam.domain.dto.AddUserCartRequest
import com.meesam.domain.dto.UpdateUserCartRequest
import com.meesam.domain.dto.UserFavoriteProductRequest

class UserCartService(
    private val userCartRepository: UserCartRepository = UserCartRepository()
) {

    suspend fun addProductToCart(addUserCartRequest: AddUserCartRequest): Unit{
        return userCartRepository.addProductToCart(addUserCartRequest)
    }

    suspend fun removeProductFromCart(updateUserCartRequest: UpdateUserCartRequest): Unit{
        return userCartRepository.removeProductFromCart(updateUserCartRequest)
    }

    suspend fun addUserFavouriteProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit {
        return userCartRepository.addUserFavouriteProduct(userFavoriteProductRequest)
    }

    suspend fun removeUserFavouriteProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit{
        return userCartRepository.removeUserFavouriteProduct(userFavoriteProductRequest)
    }

    suspend fun addUserWishlistProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit{
        return userCartRepository.addUserWishlistProduct(userFavoriteProductRequest)
    }

    suspend fun removeUserWishlistProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit{
        return userCartRepository.removeUserWishlistProduct(userFavoriteProductRequest)
    }
}