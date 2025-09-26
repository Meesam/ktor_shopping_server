package com.meesam.routes

import com.meesam.data.repositories.RefreshTokenRepository
import com.meesam.domain.dto.ActivateUserByOtpRequest
import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.LoginResponse
import com.meesam.domain.dto.NewOtpRequest
import com.meesam.domain.dto.RefreshTokenRequest
import com.meesam.domain.dto.TokenResponse
import com.meesam.domain.dto.UserRequest
import com.meesam.security.TokenService
import com.meesam.services.AuthService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(service: AuthService = AuthService()) {
    val refreshRepo = RefreshTokenRepository()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val tokenService = TokenService(jwtIssuer, jwtAudience, jwtSecret)


    route("/auth") {
        route("/register"){
            post {
                val body = call.receive<UserRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.register(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/login"){
            post {
                val body = call.receive<AuthenticationRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val user = service.login(body)
                val access = tokenService.createAccessToken(user.email, user.role)
                val refresh = tokenService.createRefreshToken(user.id ?: 0, email = user.email)
                refreshRepo.save(refresh)
                call.respond(HttpStatusCode.OK,
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

        route("/refresh"){
            post {
                val req = call.receive<RefreshTokenRequest>()
                val stored = refreshRepo.findActiveByToken(req.token)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")

                // In case you need role, you can load it from DB by userId; omitted here for brevity
                val access = tokenService.createAccessToken(stored.email, role = null)
                val newRefresh = tokenService.createRefreshToken(stored.userId, stored.email)
                val user = service.getUserDetailById(stored.userId)

                // rotate: revoke the old token and save the new one
                refreshRepo.revokeByJti(stored.jti, replacedBy = newRefresh.jti)
                refreshRepo.save(newRefresh)

                call.respond(HttpStatusCode.OK,
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

        route("/logout"){
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

        route("/activateUserByOtp"){
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

        route("/generateNewOtp"){
            post {
                val body = call.receive<NewOtpRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.generateNewOtp(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

    }
}