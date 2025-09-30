package com.meesam.routes

import com.meesam.data.repositories.RefreshTokenRepository
import com.meesam.domain.dto.ActivateUserByOtpRequest
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.ForgotPasswordRequest
import com.meesam.domain.dto.LoginResponse
import com.meesam.domain.dto.NewOtpRequest
import com.meesam.domain.dto.RefreshTokenRequest
import com.meesam.domain.dto.ResetPasswordRequest
import com.meesam.domain.dto.TokenResponse
import com.meesam.domain.dto.UserRequest
import com.meesam.plugins.EmailServiceKey
import com.meesam.security.TokenService
import com.meesam.services.AuthService
import com.meesam.services.EmailDetails
import com.meesam.services.EmailService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.application


fun Route.authRoutes(
    service: AuthService = AuthService(),

    ) {
    val refreshRepo = RefreshTokenRepository()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val tokenService = TokenService(jwtIssuer, jwtAudience, jwtSecret)
    val emailService: EmailService = application.attributes[EmailServiceKey]

    fun setAuthCookies(
        accessToken: String,
        accessMaxAgeSeconds: Long,
        refreshToken: String,
        refreshMaxAgeSeconds: Long,
        call: RoutingCall
    ) {
        call.response.cookies.append(
            Cookie(
                name = "access_token",
                value = accessToken,
                httpOnly = true,
                secure = false, // set false only for local HTTP testing
                path = "/",
                maxAge = accessMaxAgeSeconds.toInt(),
                encoding = CookieEncoding.RAW
            )
        )
        call.response.cookies.append(
            Cookie(
                name = "refresh_token",
                value = refreshToken,
                httpOnly = true,
                secure = false,
                path = "/",
                maxAge = refreshMaxAgeSeconds.toInt(),
                encoding = CookieEncoding.RAW
            )
        )
    }

    fun clearAuthCookies(call: RoutingCall) {
        call.response.cookies.append(
            Cookie(name = "access_token", value = "", maxAge = 0, path = "/", httpOnly = true, secure = true)
        )
        call.response.cookies.append(
            Cookie(name = "refresh_token", value = "", maxAge = 0, path = "/", httpOnly = true, secure = true)
        )
    }


    route("/auth") {
        route("/register") {
            post {
                val body = call.receive<UserRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.register(body)
                val emailDetails = EmailDetails(
                    toAddress = body.email.trim().lowercase(),
                    subject = "OTP for activate account in Spring Shopping",
                    body = result.otp.toString()
                )

                emailService.sendSimpleEmail(emailDetails)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/login") {
            post {
                val body = call.receive<AuthenticationRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val user = service.login(body)
                user.let {
                    val access = tokenService.createAccessToken(user.email, user.role)
                    val refresh = tokenService.createRefreshToken(user.id ?: 0, email = user.email)
                    refreshRepo.save(refresh)
                    val accessTtlSeconds =
                        (access.expiresAt.epochSecond - java.time.Instant.now().epochSecond).coerceAtLeast(1)
                    val refreshTtlSeconds =
                        (refresh.expiresAt.epochSecond - java.time.Instant.now().epochSecond).coerceAtLeast(1)
                    setAuthCookies(
                        call = call,
                        accessToken = access.token,
                        accessMaxAgeSeconds = accessTtlSeconds,
                        refreshToken = refresh.token,
                        refreshMaxAgeSeconds = refreshTtlSeconds
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        LoginResponse(
                            accessToken = access.token,
                            accessTokenExpiresAt = access.expiresAt.toString(),
                            refreshToken = refresh.token,
                            refreshTokenExpiresAt = refresh.expiresAt.toString(),
                            user = user
                        )
                    )
                }
            }
        }

        route("/refresh") {
            post {
                val body = call.receive<RefreshTokenRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val stored = refreshRepo.findActiveByToken(body.token)
                stored?.let {
                    val access = tokenService.createAccessToken(stored.email, role = null)
                    val newRefresh = tokenService.createRefreshToken(stored.userId, stored.email)
                    val user = service.getUserDetailById(stored.userId)

                    // rotate: revoke the old token and save the new one
                    refreshRepo.revokeByJti(stored.jti, replacedBy = newRefresh.jti)
                    refreshRepo.save(newRefresh)
                    val accessTtlSeconds =
                        (access.expiresAt.epochSecond - java.time.Instant.now().epochSecond).coerceAtLeast(1)
                    val refreshTtlSeconds =
                        (newRefresh.expiresAt.epochSecond - java.time.Instant.now().epochSecond).coerceAtLeast(1)
                    setAuthCookies(
                        call = call,
                        accessToken = access.token,
                        accessMaxAgeSeconds = accessTtlSeconds,
                        refreshToken = newRefresh.token,
                        refreshMaxAgeSeconds = refreshTtlSeconds
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        TokenResponse(
                            accessToken = access.token,
                            accessTokenExpiresAt = access.expiresAt.toString(),
                            refreshToken = newRefresh.token,
                            refreshTokenExpiresAt = newRefresh.expiresAt.toString(),
                            user = user
                        )
                    )
                }
            }
        }

        route("/logout") {
            post {
                val req = call.receiveNullable<RefreshTokenRequest>()
                req?.token?.let { rt ->
                    val active = refreshRepo.findActiveByToken(rt)
                    if (active != null) {
                        refreshRepo.revokeByJti(active.jti)
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }

        route("/activateUserByOtp") {
            post {
                val body = call.receive<ActivateUserByOtpRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                service.activateUserByOtp(body)
                call.respond(HttpStatusCode.OK, "User activated successfully")
            }
        }

        route("/generateNewOtp") {
            post {
                val body = call.receive<NewOtpRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.generateNewOtp(body)
                val emailDetails = EmailDetails(
                    toAddress = body.email.trim().lowercase(),
                    subject = "New OTP for activate account in Spring Shopping",
                    body = result.otp.toString()
                )
                emailService.sendSimpleEmail(emailDetails)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/forgotPassword") {
            post {
                val appConfig = application.environment.config.config("ktor.app")
                val frontendUrl = appConfig.property("frontendBaseUrl").getString()
                val body = call.receive<ForgotPasswordRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.forgotPassword(body)
                val resetLink = "$frontendUrl/changePassword?email=$result"
                val emailDetails = EmailDetails(
                    toAddress = body.email.trim().lowercase(),
                    subject = "Forgot password request",
                    body = """
                        You requested a password reset. 
                        Click the following link to reset your password:
                        $resetLink
                    """.trimIndent()
                )
                emailService.sendSimpleEmail(emailDetails)
                call.respond(HttpStatusCode.OK, "Forgot password request link send to your email")
            }
        }

        route("/resetPassword") {
            post {
                val body = call.receive<ResetPasswordRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.resetPassword(body)
                val emailDetails = EmailDetails(
                    toAddress = body.email.trim().lowercase(),
                    subject = "OTP for activate account in Spring Shopping",
                    body = result.otp.toString()
                )
                emailService.sendSimpleEmail(emailDetails)
                call.respond(HttpStatusCode.Created, result)
            }
        }
    }
}