package com.meesam.plugins

import com.meesam.domain.dto.ErrorResponse
import com.meesam.domain.exceptionhandler.ActiveAccountException
import com.meesam.domain.exceptionhandler.ConflictException
import com.meesam.domain.exceptionhandler.DomainException
import com.meesam.domain.exceptionhandler.InvalidCredentialsException
import com.meesam.domain.exceptionhandler.InvalidOtpException
import com.meesam.domain.exceptionhandler.OtpExpiredException
import com.meesam.domain.exceptionhandler.ResourceNotFoundException
import com.meesam.domain.exceptionhandler.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
data class ErrorResponse(val message: String)


fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<InvalidCredentialsException> { call, cause ->
            val responseStatus = HttpStatusCode.Unauthorized // 401
            val responseBody = ErrorResponse(cause.message ?: "Invalid credentials")
            call.respond(responseStatus, responseBody)
        }

        exception<ActiveAccountException> { call, cause ->
            val responseStatus = HttpStatusCode.Unauthorized // 401
            val responseBody = ErrorResponse(cause.message ?: "Account is not active")
            call.respond(responseStatus, responseBody)
        }

        exception<OtpExpiredException> { call, cause ->
            val responseStatus = HttpStatusCode.Unauthorized // 401
            val responseBody = ErrorResponse(cause.message ?: "OTP is expired")
            call.respond(responseStatus, responseBody)
        }

        exception<InvalidOtpException> { call, cause ->
            val responseStatus = HttpStatusCode.Unauthorized // 401
            val responseBody = ErrorResponse(cause.message ?: "OTP is invalid")
            call.respond(responseStatus, responseBody)
        }


        exception<ResourceNotFoundException> { call, cause ->
            respondError(call, HttpStatusCode.NotFound, cause.message ?: "Not found")
        }
        exception<ValidationException> { call, cause ->
            respondError(
                call,
                HttpStatusCode.BadRequest,
                cause.message ?: "Validation failed",
                details = cause.fieldErrors
            )
        }
        exception<ConflictException> { call, cause ->
            respondError(call, HttpStatusCode.Conflict, cause.message ?: "Conflict")
        }
        exception<DomainException> { call, cause ->
            respondError(call, HttpStatusCode.BadRequest, cause.message ?: "Bad request")
        }

        // Common framework/runtime exceptions
        exception<io.ktor.server.plugins.BadRequestException> { call, cause ->
            respondError(call, HttpStatusCode.BadRequest, cause.message ?: "Bad request")
        }
        exception<io.ktor.server.plugins.NotFoundException> { call, cause ->
            respondError(call, HttpStatusCode.NotFound, cause.message ?: "Not found")
        }
        exception<SerializationException> { call, cause ->
            respondError(call, HttpStatusCode.BadRequest, "Invalid request payload: ${cause.message}")
        }

        // Catch-all: internal server errors
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)
            respondError(call, HttpStatusCode.InternalServerError, cause.message ?: "Internal server error")
        }


    }
}

private suspend fun respondError(
    call: ApplicationCall,
    status: HttpStatusCode,
    message: String,
    details: Map<String, String>? = null
) {
    val path = runCatching { call.request.uri }.getOrNull()
    call.respond(
        status,
        ErrorResponse(
            status = status.value,
            error = status.description,
            message = message,
            path = path,
            details = details
        )
    )
}
