package com.meesam.routes

import com.meesam.domain.dto.UserRequest
import com.meesam.services.AuthService
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
                val result = service.register(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }
    }
}