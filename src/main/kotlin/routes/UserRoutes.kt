package com.meesam.routes

import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.services.AuthService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoutes(service: AuthService = AuthService()) {
    route("/user") {
        route("/changePassword"){
            post {
                val body = call.receive<ChangePasswordRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val targetEmail = body.email.trim().lowercase()
                val principal = call.principal<JWTPrincipal>() ?: error("No principal")
                val tokenEmail = principal.payload.subject.trim().lowercase()
                if (tokenEmail != targetEmail) {
                    call.respond(HttpStatusCode.Forbidden, "You are not allowed to change password of other users")
                }
                service.changePassword(body)
                call.respond(HttpStatusCode.OK, "Password changed successfully")
            }
        }
    }

}