package com.meesam.routes

import com.meesam.domain.dto.ProductAttributeRequest
import com.meesam.domain.dto.UpdateProductAttributeRequest
import com.meesam.services.ProductAttributeService
import com.meesam.utills.BeanValidation
import com.meesam.utills.requireAdminOrRespond
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.text.toLongOrNull

fun Route.productAttributeRoutes(service: ProductAttributeService = ProductAttributeService()) {
    route("/productAttribute") {
        route("/create") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<ProductAttributeRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.addProductAttribute(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/update") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<UpdateProductAttributeRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.updateProductAttribute(body)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/delete") {
            delete {
                call.requireAdminOrRespond() ?: return@delete
                val attributeId = call.request.queryParameters["attributeId"]?.toLongOrNull() ?: -1
                val result = service.deleteProductAttribute(attributeId)
                call.respond(HttpStatusCode.OK, result)
            }
        }
    }
}