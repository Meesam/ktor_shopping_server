package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.CartProductsTable
import com.meesam.data.tables.ProductTable
import com.meesam.data.tables.UserFavProductTable
import com.meesam.data.tables.UserTable
import com.meesam.data.tables.UserWishListTable
import com.meesam.domain.dto.AddUserCartRequest
import com.meesam.domain.dto.UpdateUserCartRequest
import com.meesam.domain.dto.UserFavoriteProductRequest
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

class UserCartRepository {

    suspend fun addProductToCart(addUserCartRequest: AddUserCartRequest): Unit = dbQuery {
        try {
            val updateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            /** Check if the product is already in cart or not for the user **/
            val row = CartProductsTable.select(CartProductsTable.id)
                .where {
                    CartProductsTable.productId eq addUserCartRequest.productId and (CartProductsTable.userId eq addUserCartRequest.userId)
                }.singleOrNull()
            row?.let {
                CartProductsTable.update(where = { CartProductsTable.id eq row[CartProductsTable.id] }) {
                    it[quantity] = addUserCartRequest.quantity
                }
            } ?: CartProductsTable.insert {
                it[productId] = addUserCartRequest.productId
                it[quantity] = addUserCartRequest.quantity
                it[userId] = addUserCartRequest.userId
                it[updatedAt] = updateTime
            }

        } catch (ex: ExposedSQLException) {
            throw DomainException("Unable to add product to cart")
        } catch (ex: Exception) {
            throw DomainException("Unable to add product to cart")
        }
    }

    suspend fun removeProductFromCart(updateUserCartRequest: UpdateUserCartRequest): Unit = dbQuery {
        try {
            CartProductsTable.select(CartProductsTable.id)
                .where {
                    CartProductsTable.productId eq updateUserCartRequest.productId and (CartProductsTable.userId eq updateUserCartRequest.userId)
                }.singleOrNull() ?: throw ResourceNotFoundException("Product not found in cart")

            CartProductsTable.deleteWhere {
                CartProductsTable.productId eq updateUserCartRequest.productId and (CartProductsTable.userId eq updateUserCartRequest.userId)
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun addUserFavouriteProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit = dbQuery {
        try {
            ProductTable.select(ProductTable.id)
                .where { ProductTable.id eq userFavoriteProductRequest.productId and (ProductTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("Product not found")

            UserTable.select(UserTable.id)
                .where { UserTable.id eq userFavoriteProductRequest.userId and (UserTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserFavProductTable.insert {
                it[productId] = userFavoriteProductRequest.productId
                it[userId] = userFavoriteProductRequest.userId
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun removeUserFavouriteProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit = dbQuery {
        try {
            ProductTable.select(ProductTable.id)
                .where { ProductTable.id eq userFavoriteProductRequest.productId and (ProductTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("Product not found")

            UserTable.select(UserTable.id)
                .where { UserTable.id eq userFavoriteProductRequest.userId and (UserTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserFavProductTable.deleteWhere {
                UserFavProductTable.productId eq userFavoriteProductRequest.productId and (UserFavProductTable.userId eq userFavoriteProductRequest.userId)
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun addUserWishlistProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit = dbQuery {
        try {
            ProductTable.select(ProductTable.id)
                .where { ProductTable.id eq userFavoriteProductRequest.productId and (ProductTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("Product not found")

            UserTable.select(UserTable.id)
                .where { UserTable.id eq userFavoriteProductRequest.userId and (UserTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserWishListTable.insert {
                it[productId] = userFavoriteProductRequest.productId
                it[userId] = userFavoriteProductRequest.userId
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun removeUserWishlistProduct(userFavoriteProductRequest: UserFavoriteProductRequest): Unit = dbQuery {
        try {
            ProductTable.select(ProductTable.id)
                .where { ProductTable.id eq userFavoriteProductRequest.productId and (ProductTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("Product not found")

            UserTable.select(UserTable.id)
                .where { UserTable.id eq userFavoriteProductRequest.userId and (UserTable.isActive eq true) }
                .singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserWishListTable.deleteWhere {
                UserWishListTable.productId eq userFavoriteProductRequest.productId and (UserWishListTable.userId eq userFavoriteProductRequest.userId)
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }
}