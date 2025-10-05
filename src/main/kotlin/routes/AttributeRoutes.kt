package com.meesam.routes

import com.meesam.domain.dto.AttributeRequest
import com.meesam.domain.dto.UpdateAttributeRequest
import com.meesam.services.AttributeService
import com.meesam.utills.BeanValidation
import com.meesam.utills.requireAdminOrRespond
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.text.toLongOrNull

fun Route.attributeRoutes(service: AttributeService = AttributeService()) {
    route("/attribute") {
        route("/create") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<AttributeRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.createAttribute(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/update") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<UpdateAttributeRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.updateAttribute(body)
                call.respond(HttpStatusCode.OK, result)

            }
        }

        route("/delete") {
            delete {
                call.requireAdminOrRespond() ?: return@delete
                val attributeId = call.request.queryParameters["attributeId"]?.toLongOrNull() ?: -1
                val result = service.deleteAttribute(attributeId)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/getAll") {
            get {
                val result = service.getAllAttribute()
                call.respond(HttpStatusCode.OK, result)
            }
        }
    }
}