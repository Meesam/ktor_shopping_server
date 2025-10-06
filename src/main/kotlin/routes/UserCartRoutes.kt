package com.meesam.routes

import com.meesam.domain.dto.AddUserCartRequest
import com.meesam.domain.dto.UpdateUserCartRequest
import com.meesam.domain.dto.UserFavoriteProductRequest
import com.meesam.services.UserCartService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.userCartRoutes(userService: UserCartService = UserCartService()) {
    route("/usercart") {
        route("/addToCart") {
            post {
                val body = call.receive<AddUserCartRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.addProductToCart(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/removeFromCart") {
            post {
                val body = call.receive<UpdateUserCartRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.removeProductFromCart(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/addToFavorite") {
            post {
                val body = call.receive<UserFavoriteProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.addUserFavouriteProduct(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/removeFavorite") {
            post {
                val body = call.receive<UserFavoriteProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.removeUserFavouriteProduct(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/addToWishList") {
            post {
                val body = call.receive<UserFavoriteProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.addUserWishlistProduct(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/removeWishList") {
            post {
                val body = call.receive<UserFavoriteProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.removeUserWishlistProduct(body)
                call.respond(HttpStatusCode.OK)
            }
        }


    }
}