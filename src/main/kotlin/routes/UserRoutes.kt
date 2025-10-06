package com.meesam.routes

import com.meesam.domain.dto.ChangePasswordRequest
import com.meesam.domain.dto.DeleteProductFileRequest
import com.meesam.domain.dto.DeleteProfilePictureRequest
import com.meesam.domain.dto.UserResponse
import com.meesam.domain.dto.UserUpdateRequest
import com.meesam.services.AuthService
import com.meesam.services.ProductImagesService
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
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import java.util.UUID

fun Route.userRoutes(service: AuthService = AuthService(), userService: UserService = UserService()) {
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
                    call.respond(HttpStatusCode.OK, "Password changed successfully")
                }
            }
        }

        route("/update"){
            post {
                val multipart = call.receiveMultipart()
                var userId: Long? = null
                var name: String? = null
                var fileName:String? = null
                var contentType: String? = null
                var fileBytes: ByteArray? = null
                var dob: kotlinx.datetime.LocalDate? = null
                var result: UserResponse? = null
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            val valueType = part.name.toString()
                            when(valueType){
                               "userId" -> {
                                  userId = part.value.toLong()
                               }
                              "name" -> {
                                  name = part.value
                              }
                             "dob" -> {
                                  dob = kotlinx.datetime.LocalDate.parse(part.value)
                              }
                            }
                        }
                        is PartData.FileItem ->{
                            fileName = part.originalFileName ?: UUID.randomUUID().toString()
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        else -> {}
                    }
                    if(userId !=null && fileName !=null && contentType !=null && fileBytes !=null) {
                        val updateUserRequest = UserUpdateRequest(
                            id = userId,
                            name = name,
                            dob = dob
                        )
                       result =  userService.updateUserDetails(updateUserRequest, fileBytes, fileName, contentType)
                    }
                    part.dispose() // Important: Dispose of the part to free resources
                }
                call.respond(HttpStatusCode.OK, result ?: "")
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
    }



}