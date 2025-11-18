package com.meesam.routes

import com.meesam.domain.dto.AddUserCardRequest
import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.DeleteProductFileRequest
import com.meesam.domain.dto.DeleteProfilePictureRequest
import com.meesam.domain.dto.TogglePrimaryAddressRequest
import com.meesam.domain.dto.UpdateUserAddressRequest
import com.meesam.domain.dto.UserAddressRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.services.AuthService
import com.meesam.services.ProductImagesService
import com.meesam.services.UserCardService
import com.meesam.services.UserService
import com.meesam.utills.BeanValidation
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import java.io.File
import java.util.UUID

fun Route.userRoutes(service: AuthService = AuthService(), userService: UserService = UserService(), userCardService: UserCardService = UserCardService()) {
    val config = environment.config
    val host = config.property("ktor.deployment.host").getString()
    val port = config.property("ktor.deployment.port").getString()

    route("/user") {
        route("/changePassword") {
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
                } else {
                    service.changePassword(body)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }

        route("/update"){
            post {
                try {
                    val body = call.receive<UserUpdateRequest>()
                    val errors = BeanValidation.errorsFor(body)
                    if (errors.isNotEmpty()) {
                        call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                        return@post
                    }
                    val result =  userService.updateUserDetails(body, null)
                    call.respond(HttpStatusCode.OK, result ?: "")
                }catch (e:Exception){
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }

            }
        }

        route("/addProfileImage"){
            post {
                val multipart = call.receiveMultipart()
                var userId: Long? = null
                var fileName:String? = null
                var result: UserResponse? = null
                var fileUrl: String? = null
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            val valueType = part.name.toString()
                            when(valueType){
                                "userId" -> {
                                    userId = part.value.toLong()
                                }
                            }
                        }
                        is PartData.FileItem ->{
                            fileName = part.originalFileName ?: UUID.randomUUID().toString()

                            val uploadDir = File("uploads/images")
                            uploadDir.mkdirs()
                            val file = File(uploadDir, fileName) // Save to a public directory
                            // Write the bytes to the file
                            part.streamProvider().use { inputStream ->
                                file.outputStream().buffered().use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                            // Construct the public URL
                            fileUrl =  "$host:$port/images/$fileName"
                        }
                        else -> {}
                    }
                    if(userId !=null && fileUrl !=null) {
                        val updateUserRequest = UserUpdateRequest(
                            id = userId,
                        )
                        result =  userService.addUserProfileImage(updateUserRequest, fileUrl)
                    }
                    part.dispose() // Important: Dispose of the part to free resources
                }
                val user = userService.getUserDetails(userId!!)
                call.respond(HttpStatusCode.OK, user)
            }
        }

        route("/deleteProfileImage"){
            post {
                val body = call.receive<DeleteProfilePictureRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = userService.deleteProfilePicture(body)
                call.respond(HttpStatusCode.OK, result ?: "")
            }
        }

        route("/addAddress"){
            post {
                val body = call.receive<UserAddressRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.addUserAddress(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/deleteAddress"){
            delete {
                val addressId = call.request.queryParameters["addressId"]?.toLongOrNull() ?: -1
                userService.deleteUserAddress(addressId)
                call.respond(HttpStatusCode.NoContent)
            }
        }

        route("/getAllAddress"){
            get {
                val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: -1
                val result = userService.getAllUserAddress(userId)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/updateAddress"){
            post {
                val body = call.receive<UpdateUserAddressRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.updateUserAddress(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/togglePrimaryAddress"){
            post {
                val body = call.receive<TogglePrimaryAddressRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userService.togglePrimaryAddress(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/addNewCard"){
            post {
                val body = call.receive<AddUserCardRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                userCardService.addNewCard(body)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/getAllCard"){
            get {
                val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: -1
                val result = userCardService.getUserCards(userId)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/deleteCard"){
            delete {
                val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: -1
                userService.deleteUserAddress(userId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}