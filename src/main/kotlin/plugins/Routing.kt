package com.meesam.plugins

import com.meesam.routes.attributeRoutes
import com.meesam.routes.authRoutes
import com.meesam.routes.categoryRoutes
import com.meesam.routes.productAttributeRoutes
import com.meesam.routes.productRoutes
import com.meesam.routes.userCartRoutes
import com.meesam.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            get("/health-check") {
                call.respondText("Server is running, ready to handle requests")
            }
            authRoutes()
            authenticate("auth-jwt") {
                categoryRoutes()
                userRoutes()
                attributeRoutes()
                productRoutes()
                productAttributeRoutes()
                userCartRoutes()
            }
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            // Optional: Customize Swagger UI, e.g., specify a different version
            version = "4.15.5"
        }

        staticFiles("/images", File("uploads/images")) {
            // Optional: you can configure caching, etc. here
        }
    }
}
