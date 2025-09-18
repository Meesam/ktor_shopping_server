package com.meesam.plugins

import com.meesam.routes.authRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            get("/health-check") {
                call.respondText("Server is running, ready to handle requests")
            }
            authRoutes()
        }

    }
}
