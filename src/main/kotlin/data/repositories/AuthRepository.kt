package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.UserRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.exceptionhandler.ConflictException
import com.meesam.domain.exceptionhandler.DomainException
import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update


class AuthRepository {
    suspend fun login(authenticationRequest: AuthenticationRequest): UserResponse = dbQuery {
        val normalizedEmail = authenticationRequest.email.trim().lowercase()
        val row = UserTable
            .selectAll()
            .where { UserTable.email eq normalizedEmail }
            .limit(1)
            .singleOrNull() ?: throw NotFoundException("Invalid credentials")

        val storedHash = row[UserTable.password]
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        try {
            val ok = argon2.verify(storedHash, authenticationRequest.password.toCharArray())
            if (!ok) {
                throw NotFoundException("Invalid credentials")
            } else {
                UserTable.update({ UserTable.email eq authenticationRequest.email.trim().lowercase() }) {
                    it[lastLoginAt] = CurrentDateTime
                }
            }
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
        UserResponse(
            id = row[UserTable.id],
            name = row[UserTable.name],
            email = row[UserTable.email],
            role = row[UserTable.role],
        )
    }

    suspend fun register(userRequest: UserRequest): UserResponse = dbQuery {
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        val passwordHash = argon2.hash(3, 1 shl 16, 1, userRequest.password.toCharArray())
        try {
            val insertedUser = UserTable.insert {
                it[name] = userRequest.name
                it[email] = userRequest.email.trim().lowercase()
                it[password] = passwordHash
                it[role] = userRequest.role ?: "User"
                userRequest.dob?.let { v ->
                    it[dob] = v.toLocalDateTime(TimeZone.UTC).date
                }
            }
            val id = insertedUser.resultedValues?.singleOrNull()?.get(UserTable.id)
                ?: error("Failed to retrieve generated id for user")
            UserResponse(
                id = id,
                name = userRequest.name,
                email = userRequest.email,
                role = userRequest.role ?: "User",

                )
        } catch (e: ExposedSQLException) {
            if (e.sqlState == "23505") {
                throw ConflictException("Email '${userRequest.email}' already exists")
            }
            throw DomainException(e.message.toString())
        }
    }

    suspend fun changePassword(passwordRequest: ChangePasswordRequest) = dbQuery {
        val user =
            UserTable.selectAll().where { UserTable.email eq passwordRequest.email.trim().lowercase() }.singleOrNull()
                ?: throw NotFoundException("Email ${passwordRequest.email} not found")
        val storedHash = user[UserTable.password]
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        try {
            val ok = argon2.verify(storedHash, passwordRequest.oldPassword.toCharArray())
            if (!ok) {
                throw NotFoundException("Invalid old password")
            } else {
                val newPasswordHash = argon2.hash(3, 1 shl 16, 1, passwordRequest.newPassword.toCharArray())
                UserTable.update({ UserTable.email eq passwordRequest.email.trim().lowercase() }) {
                    it[password] = newPasswordHash
                }
            }
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }
}


