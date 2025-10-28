package com.meesam

import com.meesam.data.db.DatabaseFactory
import com.meesam.plugins.EmailServiceKey
import com.meesam.plugins.configureFirebase
import com.meesam.plugins.configureHTTP
import com.meesam.plugins.configureMonitoring
import com.meesam.plugins.configureRouting
import com.meesam.plugins.configureSecurity
import com.meesam.plugins.configureSerialization
import com.meesam.plugins.configureStatusPages
import com.meesam.plugins.loadEmailConfig
import com.meesam.services.EmailService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment)
    val config = loadEmailConfig()
    val emailService = EmailService(config)
    attributes.put(EmailServiceKey, emailService)
    configureSerialization()
    configureStatusPages()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureFirebase()
    //RedisClientManager.initialize()
    //environment.monitor.subscribe(ApplicationStopping) {
       // RedisClientManager.close()
   // }
    configureRouting()
}


