package com.meesam.utills

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond


suspend fun ApplicationCall.requireAdminOrRespond(): JWTPrincipal? {
    val p = this.principal<JWTPrincipal>() ?: run {
        respond(HttpStatusCode.Unauthorized); return null
    }
    if (!requireAdmin(p)) {
        respond(HttpStatusCode.Forbidden, "Admins only"); return null
    }
    return p
}

fun requireAdmin(principal: JWTPrincipal): Boolean =
    principal.payload.getClaim("role").asString() == "Admin"