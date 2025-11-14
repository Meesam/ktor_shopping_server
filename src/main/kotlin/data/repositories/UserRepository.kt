package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserAddressTable
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.TogglePrimaryAddressRequest
import com.meesam.domain.dto.UpdateUserAddressRequest
import com.meesam.domain.dto.UserAddressRequest
import com.meesam.domain.dto.UserAddressResponse
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
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
                it[phone] = userUpdateRequest.phone ?: user[UserTable.phone]
            }

            UserResponse(
                id = userUpdateRequest.id,
                name = userUpdateRequest.name ?: user[UserTable.name],
                email = user[UserTable.email],
                role = user[UserTable.role],
                profilePicUrl = userUpdateRequest.profilePicUrl ?: user[UserTable.profilePicUrl],
                dob = userUpdateRequest.dob ?: user[UserTable.dob],
                lastLoginAt = user[UserTable.lastLoginAt],
                phone = userUpdateRequest.phone ?: user[UserTable.phone]
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
                }.singleOrNull()?.let { true } ?: false
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
                profilePicUrl = user[UserTable.profilePicUrl],
                dob = user[UserTable.dob],
                lastLoginAt = user[UserTable.lastLoginAt]
            )
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
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
                it[contactName] = userAddressRequest.contactName
                it[contactNumber] = userAddressRequest.contactNumber
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun deleteUserAddress(addressId: Long): Unit = dbQuery {
        try {
            UserAddressTable.select(UserAddressTable.id).where { UserAddressTable.id eq addressId }.singleOrNull()
                ?: throw ResourceNotFoundException("Address not found")
            UserAddressTable.deleteWhere {
                UserAddressTable.id eq addressId
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun updateUserAddress(userAddressRequest: UpdateUserAddressRequest): Unit = dbQuery {
        try {
            val isUserExistedDB = isUserExists(userAddressRequest.userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserAddressTable.update(where = { UserAddressTable.id eq userAddressRequest.id }) {
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
                it[contactName] = userAddressRequest.contactName
                it[contactNumber] = userAddressRequest.contactNumber
            }
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun getUserDetails(id: Long): UserResponse = dbQuery {
        try {
            val isUserExistedDB = isUserExists(id)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            val user = UserTable.selectAll().where {
                UserTable.id eq id and (UserTable.isActive eq true)
            }.singleOrNull() ?: throw ResourceNotFoundException("User not found")
            UserResponse(
                id = user[UserTable.id],
                name = user[UserTable.name],
                email = user[UserTable.email],
                role = user[UserTable.role],
                profilePicUrl = user[UserTable.profilePicUrl],
                dob = user[UserTable.dob],
                lastLoginAt = user[UserTable.lastLoginAt],
                phone = user[UserTable.phone]
            )
        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun getAllUserAddress(userId: Long): List<UserAddressResponse> = dbQuery {
        try {
            val isUserExistedDB = isUserExists(userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserAddressTable.selectAll().where { UserAddressTable.userId eq userId }.map { address ->
                UserAddressResponse(
                    id = address[UserAddressTable.id],
                    userId = address[UserAddressTable.userId],
                    street = address[UserAddressTable.street],
                    state = address[UserAddressTable.state],
                    city = address[UserAddressTable.city],
                    pin = address[UserAddressTable.zipCode],
                    country = address[UserAddressTable.country],
                    isPrimary = address[UserAddressTable.isPrimary],
                    contactName = address[UserAddressTable.contactName],
                    contactNumber = address[UserAddressTable.contactNumber],
                    nearBy = address[UserAddressTable.nearBy],
                    addressType = address[UserAddressTable.addressType]
                )
            }.toList()

        } catch (ex: ResourceNotFoundException) {
            throw ResourceNotFoundException(ex.message.toString())
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (ex: Exception) {
            throw DomainException(ex.message.toString())
        }
    }

    suspend fun togglePrimaryAddress(togglePrimaryAddressRequest: TogglePrimaryAddressRequest): Unit = dbQuery {
        try {
            val isUserExistedDB = isUserExists(togglePrimaryAddressRequest.userId)
            if (!isUserExistedDB) {
                throw ResourceNotFoundException("User not found")
            }
            UserAddressTable.select(UserAddressTable.id)
                .where { UserAddressTable.id eq togglePrimaryAddressRequest.addressId }.singleOrNull()
                ?: throw ResourceNotFoundException("Address not found")

            UserAddressTable.update(where = { UserAddressTable.id eq togglePrimaryAddressRequest.addressId }) {
                it[isPrimary] = true
            }
            UserAddressTable.update(where = { UserAddressTable.id neq togglePrimaryAddressRequest.addressId and
                    (UserAddressTable.userId eq togglePrimaryAddressRequest.userId) }) {
                it[isPrimary] = false
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