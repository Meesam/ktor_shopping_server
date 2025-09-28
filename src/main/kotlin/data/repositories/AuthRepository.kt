package com.meesam.data.repositories

import com.meesam.data.db.DatabaseFactory.dbQuery
import com.meesam.data.tables.OtpTable
import com.meesam.data.tables.UserTable
import com.meesam.domain.dto.ActivateUserByOtpRequest
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.ForgotPasswordRequest
import com.meesam.domain.dto.NewOtpRequest
import com.meesam.domain.dto.OtpResponse
import com.meesam.domain.dto.UserRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.exceptionhandler.ActiveAccountException
import com.meesam.domain.exceptionhandler.ConflictException
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.InvalidCredentialsException
import com.meesam.domain.exceptionhandler.InvalidOtpException
import com.meesam.domain.exceptionhandler.OtpExpiredException
import com.meesam.services.EmailDetails
import com.meesam.services.sendSimpleEmail
import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import kotlin.time.Duration.Companion.minutes


class AuthRepository {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthRepository::class.java)
    }

    private val secureRandom = SecureRandom()

    private fun generateOtp(): Int {
        return secureRandom.nextInt(900_000) + 100_000
    }

    suspend fun login(authenticationRequest: AuthenticationRequest): UserResponse = dbQuery {
        val normalizedEmail = authenticationRequest.email.trim().lowercase()

        with(UserTable) {
            /* Check if email exists */
            val row =
                selectAll()
                    .where { email eq normalizedEmail and (isActive eq true) }
                    .limit(1)
                    .singleOrNull() ?: throw InvalidCredentialsException()

            /* Check for Active User */
            selectAll()
                .where { (email eq normalizedEmail) and (isActivatedByOtp eq true) }
                .limit(1)
                .singleOrNull() ?: throw ActiveAccountException()



            val storedHash = row[password]
            val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
            try {
                val ok = argon2.verify(storedHash, authenticationRequest.password.toCharArray())
                if (!ok) {
                    throw InvalidCredentialsException()
                } else {
                    UserTable.update({ email eq authenticationRequest.email.trim().lowercase() }) {
                        it[lastLoginAt] = CurrentDateTime
                    }
                }
            }
            catch (_: InvalidCredentialsException) {
                throw InvalidCredentialsException()
            }
            catch (_: ActiveAccountException) {
                throw ActiveAccountException()
            }
            catch (e: Exception) {
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

               val insertedOtp = OtpTable.insert {
                    it[userId] = id
                    it[otp] = generateOtp()
                    it[email] = userRequest.email.trim().lowercase()
                    it[expiresAt] = Clock.System.now() + 1.minutes
                }

                val otp = insertedOtp.resultedValues?.singleOrNull()?.get(OtpTable.otp)
                    ?: throw DomainException("Failed to retrieve generated id for user")

                val emailDetails = EmailDetails(
                    toAddress = userRequest.email.trim().lowercase(),
                    subject = "OTP for activate account in Spring Shopping",
                    body = otp.toString()
                )
                sendSimpleEmail(emailDetails)

                UserResponse(
                    id = id,
                    name = userRequest.name,
                    email = userRequest.email,
                    role = userRequest.role ?: "User",
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
            passwordRequest.oldPassword?.let {
                val ok = argon2.verify(storedHash, passwordRequest.oldPassword.toCharArray())
                if (!ok) {
                    throw NotFoundException("Invalid old password")
                }
            }
            val newPasswordHash = argon2.hash(3, 1 shl 16, 1, passwordRequest.newPassword.toCharArray())
            UserTable.update({ UserTable.email eq passwordRequest.email.trim().lowercase() }) {
                it[password] = newPasswordHash
            }

        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Unit = dbQuery {
        val normalizedEmail = forgotPasswordRequest.email.trim().lowercase()

        val user =
            UserTable.selectAll().where { UserTable.email eq normalizedEmail }.singleOrNull()
                ?: throw NotFoundException("Email ${forgotPasswordRequest.email} not found")

        var forGotPasswordLink: String = ""

        val emailDetails = EmailDetails(
            toAddress = normalizedEmail,
            subject = "New OTP for activate account in Spring Shopping",
            body = forGotPasswordLink
        )
        sendSimpleEmail(emailDetails)

    }

    suspend fun activateUserByOtp(activateUserByOtpRequest: ActivateUserByOtpRequest): Unit = dbQuery {
        try {
            val user = UserTable.selectAll().where {
                UserTable.email eq activateUserByOtpRequest.email
            }.singleOrNull()
                ?: throw NotFoundException("EmailId ${activateUserByOtpRequest.email} not found")

            OtpTable.apply {
                selectAll()
                    .where {
                        (email eq activateUserByOtpRequest.email) and
                                (userId eq user[UserTable.id]) and
                                (otp eq activateUserByOtpRequest.otp)
                    }.singleOrNull()
                    ?: throw InvalidOtpException()

                val row = selectAll()
                    .where {
                        (email eq activateUserByOtpRequest.email) and
                                (userId eq user[UserTable.id]) and
                                (otp eq activateUserByOtpRequest.otp) and
                        (expiresAt greaterEq Clock.System.now())
                    }.singleOrNull()
                    ?: throw OtpExpiredException()

                UserTable.update({ UserTable.email eq activateUserByOtpRequest.email }) {
                    it[isActivatedByOtp] = true
                }
            }

        }
        catch (_: InvalidOtpException) {
            throw InvalidOtpException()
        }
        catch (_: OtpExpiredException) {
            throw OtpExpiredException()
        }
        catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun generateNewOtp(newOtpRequest: NewOtpRequest): OtpResponse = dbQuery {
        try {
            val newOtp = generateOtp()
            val normalizedEmail = newOtpRequest.email.trim().lowercase()
            val user = UserTable.selectAll().where {
                UserTable.email eq normalizedEmail
            }.singleOrNull()
                ?: throw NotFoundException("User with ${newOtpRequest.email} not found")

            /* First Delete old expired OTP */
            with(OtpTable) {
                deleteWhere {
                    (email eq normalizedEmail) and
                            (userId eq user[UserTable.id])
                }
                /* Insert new OTP */
                 insert {
                    it[userId] = user[UserTable.id]
                    it[otp] = newOtp
                    it[email] = normalizedEmail
                    it[expiresAt] = Clock.System.now() + 1.minutes
                }

                val emailDetails = EmailDetails(
                    toAddress = normalizedEmail,
                    subject = "New OTP for activate account in Spring Shopping",
                    body = newOtp.toString()
                )
                sendSimpleEmail(emailDetails)

                OtpResponse(
                    email = normalizedEmail,
                    otp = newOtp,
                    otpSent = true
                )
            }
        } catch (ex: ExposedSQLException) {
            throw DomainException(ex.message.toString())
        } catch (e: Exception) {
            throw DomainException(e.message.toString())
        }
    }

    suspend fun getUserDetailById(userId: Long): UserResponse? = dbQuery {
        with(UserTable) {
            val row = select(id, name, email, role, dob, lastLoginAt, createdAt, profilePicUrl).where { id eq userId }
                .singleOrNull()
                ?: throw NotFoundException("User not found")

            UserResponse(
                id = row[id],
                name = row[name],
                email = row[email],
                role = row[role],
                //lastLoginAt = row[lastLoginAt] as Instant?,
                profilePicUrl = row[profilePicUrl],
            )
        }
    }
}


