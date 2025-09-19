package com.meesam.plugins

import com.meesam.routes.authRoutes
import com.meesam.routes.categoryRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            get("/health-check") {
                call.respondText("Server is running, ready to handle requests")
            }
            authRoutes()
            authenticate("auth-jwt") {
                categoryRoutes()
            }

        }

    }
}
