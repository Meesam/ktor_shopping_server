package com.meesam.plugins

import com.meesam.services.EmailConfig
import com.meesam.services.EmailService
import io.ktor.server.application.*
import io.ktor.util.AttributeKey


val EmailServiceKey = AttributeKey<EmailService>("EmailService")

fun Application.loadEmailConfig(): EmailConfig {
    val emailConfigBlock = environment.config.config("ktor.email")

    return EmailConfig(
        smtpHost = emailConfigBlock.property("smtpHost").getString(),
        smtpPort = emailConfigBlock.property("smtpPort").getString().toInt(),
        username = emailConfigBlock.property("username").getString(),
        // This is where you correctly access the configured password property:
        password = emailConfigBlock.property("password").getString(),
        senderEmail = emailConfigBlock.property("senderEmail").getString(),
        senderName = emailConfigBlock.property("senderName").getString(),
        useSSL = emailConfigBlock.property("useSSL").getString().toBoolean(),
        useTLS = emailConfigBlock.property("useTLS").getString().toBoolean()
    )
}