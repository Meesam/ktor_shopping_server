package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.OtpTable
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.ActivateUserByOtpRequest
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.NewOtpRequest
import com.meesam.domain.dto.OtpResponse
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
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.security.SecureRandom
import kotlin.time.Duration.Companion.minutes


class AuthRepository {
    private val secureRandom = SecureRandom()

    private fun generateOtp(): Int {
        return secureRandom.nextInt(900_000) + 100_000
    }

    suspend fun login(authenticationRequest: AuthenticationRequest): UserResponse = dbQuery {
        val normalizedEmail = authenticationRequest.email.trim().lowercase()

        with(UserTable) {
            /* Check for Active User */
            selectAll()
            .where { (email eq normalizedEmail) and (isActivatedByOtp eq true) }
            .limit(1)
            .singleOrNull() ?: throw NotFoundException("Check your registered email and activate your account")

            val row =
                selectAll()
                .where { email eq normalizedEmail and (isActive eq true) }
                .limit(1)
                .singleOrNull() ?: throw NotFoundException("Invalid credentials or account are not active")

            val storedHash = row[password]
            val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
            try {
                val ok = argon2.verify(storedHash, authenticationRequest.password.toCharArray())
                if (!ok) {
                    throw NotFoundException("Invalid credentials")
                } else {
                    UserTable.update({email eq authenticationRequest.email.trim().lowercase() }) {
                        it[lastLoginAt] = CurrentDateTime
                    }
                }
            } catch (e: Exception) {
                throw DomainException(e.message.toString())
            }
            UserResponse(
                id = row[id],
                name = row[name],
                email = row[email],
                role = row[role],
            )
        }
    }

    suspend fun register(userRequest: UserRequest): UserResponse = dbQuery {
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        val passwordHash = argon2.hash(3, 1 shl 16, 1, userRequest.password.toCharArray())
        try {
            with(userRequest) {
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
                    ?: throw DomainException("Failed to retrieve generated id for user")

                OtpTable.insert {
                    it[userId] = id
                    it[otp] = generateOtp()
                    it[email] = userRequest.email.trim().lowercase()
                    it[expiresAt] = Clock.System.now() + 5.minutes
                }

                UserResponse(
                    id = id,
                    name = userRequest.name,
                    email = userRequest.email,
                    role = userRequest.role ?: "User"
                )
            }
        } catch (e: ExposedSQLException) {
            if (e.sqlState == "23505") {
                throw ConflictException("Email '${userRequest.email}' already exists")
            }
            throw DomainException(e.message.toString())
        }
    }

    suspend fun changePassword(passwordRequest: ChangePasswordRequest): Unit = dbQuery {
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

    suspend fun activateUserByOtp(activateUserByOtpRequest: ActivateUserByOtpRequest): Unit = dbQuery {
        try {
            val user = UserTable.selectAll().where {
                UserTable.id eq activateUserByOtpRequest.id
            }.singleOrNull()
                ?: throw NotFoundException("UserId ${activateUserByOtpRequest.id} not found")

            OtpTable.apply {
                selectAll()
                    .where {
                        (userId eq activateUserByOtpRequest.id) and
                                (email eq user[UserTable.email]) and
                                (otp eq activateUserByOtpRequest.otp) and
                                (expiresAt greaterEq Clock.System.now())
                    }.singleOrNull()
                    ?: throw DomainException("Invalid OTP")

                UserTable.update({ UserTable.id eq activateUserByOtpRequest.id }) {
                    it[isActivatedByOtp] = true
                }
            }

        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun generateNewOtp(newOtpRequest: NewOtpRequest): OtpResponse = dbQuery {
        try {
            val user = UserTable.selectAll().where {
                UserTable.id eq newOtpRequest.userId
            }.singleOrNull()
                ?: throw NotFoundException("UserId ${newOtpRequest.userId} not found")

            /* First Delete old expired OTP */
            with(OtpTable) {
                deleteWhere {
                    (email eq user[UserTable.email]) and
                            (userId eq newOtpRequest.userId)
                }
                /* Insert new OTP */
                val insertedOtp = insert {
                    it[userId] = newOtpRequest.userId
                    it[otp] = generateOtp()
                    it[email] = user[UserTable.email].trim().lowercase()
                    it[expiresAt] = Clock.System.now() + 5.minutes
                }

                val otp = insertedOtp.resultedValues?.singleOrNull()?.get(OtpTable.otp)
                    ?: throw DomainException("Failed to retrieve generated token for user")

                OtpResponse(
                    id = newOtpRequest.userId,
                    otp = otp,
                    otpSent = true
                )
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }
}


