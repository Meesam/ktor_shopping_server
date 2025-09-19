package com.meesam.routes

import com.meesam.domain.dto.CategoryRequest
import com.meesam.services.CategoryService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.categoryRoutes(service: CategoryService = CategoryService()) {
    route("/category") {
        route("/create") {
            post {
                val body = call.receive<CategoryRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.createCategory(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/update") {
            post {
                val body = call.receive<CategoryRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.updateCategory(body)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/delete"){
            delete {
                val categoryId = call.request.queryParameters["categoryId"]?.toLongOrNull() ?: -1
                val result = service.deleteCategory(categoryId)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/getAll"){
            get {
                val result = service.getAllCategory()
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/get-by-id"){
            get {
                val categoryId = call.request.queryParameters["categoryId"]?.toLongOrNull() ?: -1
                val result = service.getCategoryById(categoryId)
                call.respond(status =  HttpStatusCode.OK, message = result ?: mapOf("message" to "Category not found"))
            }

        }
    }
}