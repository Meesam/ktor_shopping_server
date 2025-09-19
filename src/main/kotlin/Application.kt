package com.meesam

import com.meesam.data.db.DatabaseFactory
import com.meesam.plugins.configureHTTP
import com.meesam.plugins.configureMonitoring
import com.meesam.plugins.configureRouting
import com.meesam.plugins.configureSerialization
import com.meesam.plugins.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment)
    configureSerialization()
    configureStatusPages()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
