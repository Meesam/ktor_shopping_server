package com.meesam.routes

import com.meesam.domain.dto.AuthenticationRequest
import com.meesam.domain.dto.UserRequest
import com.meesam.services.AuthService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(service: AuthService = AuthService()) {
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
                val result = service.login(body)
                call.respond(HttpStatusCode.OK, result)
            }
        }
    }
}