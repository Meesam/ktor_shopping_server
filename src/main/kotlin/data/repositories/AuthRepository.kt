package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.UserRequest
import com.meesam.domain.dto.UserResponse
import de.mkammerer.argon2.Argon2Factory
import org.jetbrains.exposed.sql.insert
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException


class AuthRepository {
   /* suspend fun login(email: String, password: String): UserResponse = dbQuery {

        // Normalize input
        val normalizedEmail = email.trim().lowercase()

        // Fetch only what you need and at most one row
        val row = UserTable
            .slice(UserTable.id, UserTable.password, UserTable.name, UserTable.email, UserTable.role)
            .select { UserTable.email eq normalizedEmail }
            .limit(1)
            .singleOrNull() ?: error("Invalid credentials")

        val storedHash = row[UserTable.password]
        Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id).use { argon2 ->
            val ok = argon2.verify(storedHash, password.toCharArray())
            if (!ok) error("Invalid credentials")
        }

        // Return whatever your domain expects; avoid leaking details
        UserResponse(
            id = row[UserTable.id],
            name = row[UserTable.name],
            email = row[UserTable.email],
            role = row[UserTable.role]
        )



    }*/

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
                throw IllegalArgumentException("Email already in use")
            }
            throw e
        }

    }
}


