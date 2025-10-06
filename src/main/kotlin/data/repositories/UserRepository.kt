package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserAddressTable
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.UserAddressRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun updateUserDetails(userUpdateRequest: UserUpdateRequest): UserResponse = dbQuery {
        try {
            val user = UserTable.selectAll().where {
                UserTable.id eq userUpdateRequest.id and (UserTable.isActive eq true)
            }.singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserTable.update({ UserTable.id eq userUpdateRequest.id }) {
                it[name] = userUpdateRequest.name ?: user[UserTable.name]
                it[dob] = userUpdateRequest.dob?.let {
                    userUpdateRequest.dob
                } ?: user[UserTable.dob]
                it[profilePicUrl] = userUpdateRequest.profilePicUrl ?: user[UserTable.profilePicUrl]
            }

            UserResponse(
                id = userUpdateRequest.id,
                name = userUpdateRequest.name ?: user[UserTable.name],
                email = user[UserTable.email],
                role = user[UserTable.role],
                profilePicUrl = userUpdateRequest.profilePicUrl ?: user[UserTable.profilePicUrl],
                dob = userUpdateRequest.dob ?: user[UserTable.dob],
                lastLoginAt = user[UserTable.lastLoginAt]
            )

        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        } catch (e: ResourceNotFoundException) {
            throw ResourceNotFoundException(e.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun isUserExists(userId: Long?): Boolean = dbQuery {
        try {
            userId?.let {
                UserTable.selectAll().where {
                    UserTable.id eq userId and (UserTable.isActive eq true)
                }.singleOrNull()
                    ?.let { true } ?: false
            } ?: false

        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun deleteProfilePicture(userId: Long): UserResponse = dbQuery {
        try {
            val user = UserTable.selectAll().where {
                UserTable.id eq userId and (UserTable.isActive eq true)
            }.singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserTable.update({ UserTable.id eq userId }) {
                it[profilePicUrl] = null
            }

            UserResponse(
                id = user[UserTable.id],
                name = user[UserTable.name],
                email = user[UserTable.email],
                role = user[UserTable.role],
                profilePicUrl = null,
                dob = user[UserTable.dob],
                lastLoginAt = user[UserTable.lastLoginAt]
            )
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        }catch (ex: Exception){
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun addUserAddress(userAddressRequest: UserAddressRequest): Unit = dbQuery {
        try {
            val isUserExistedDB = isUserExists(userAddressRequest.userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserAddressTable.insert {
                it[userId] = userAddressRequest.userId
                it[addressType] = userAddressRequest.addressType
                it[street] = userAddressRequest.street
                it[city] = userAddressRequest.city
                it[state] = userAddressRequest.state
                it[country] = userAddressRequest.country
                it[zipCode] = userAddressRequest.zipCode
                it[nearBy] = userAddressRequest.nearBy
                it[comment] = userAddressRequest.comment
                it[isPrimary] = userAddressRequest.isPrimary
            }


        }catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        }catch (ex: Exception){
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun deleteUserAddress(addressId: Long):Unit = dbQuery {

    }
}