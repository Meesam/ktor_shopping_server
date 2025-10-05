package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun updateUserDetails(userUpdateRequest: UserUpdateRequest): UserResponse = dbQuery{
        try {
            val user = UserTable.selectAll().where{
                UserTable.id eq userUpdateRequest.id and (UserTable.isActive eq true)
            }.singleOrNull() ?: throw ResourceNotFoundException("User not found")

            UserTable.update({UserTable.id eq userUpdateRequest.id}){
                it[name] = userUpdateRequest.name ?: ""
                it[dob] = userUpdateRequest.dob?.let {
                    userUpdateRequest.dob
                }
                it[profilePicUrl] = userUpdateRequest.profilePicUrl ?: ""
            }

            UserResponse(
                id = user[UserTable.id],
                name = user[UserTable.name],
                email = user[UserTable.email],
                role = user[UserTable.role],
                profilePicUrl = user[UserTable.profilePicUrl]
            )

        }catch (e: Exception){
            throw DomainException(e.message.toString())
        }catch (e: ResourceNotFoundException){
            throw ResourceNotFoundException(e.message.toString())
        }catch (e: Exception){
            throw DomainException(e.message.toString())
        }


    }

}