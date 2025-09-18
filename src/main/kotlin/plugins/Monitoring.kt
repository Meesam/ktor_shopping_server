package com.meesam.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")
    }
}
