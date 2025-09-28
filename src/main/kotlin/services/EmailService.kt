package com.meesam.services

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.String

// A basic data class for email content
data class EmailDetails(
    val toAddress: String,
    val subject: String,
    val body: String
)

suspend fun sendSimpleEmail(details: EmailDetails) {
    // The email sending process is blocking, so we must switch to Dispatchers.IO
    withContext(Dispatchers.IO) {
        try {
            val email = SimpleEmail()
            val config = getEmailConfig()
            // 1. Configure SMTP Server details
            email.hostName = config.smtpHost
            email.setSmtpPort(config.smtpPort)
            email.isSSLOnConnect = config.useSSL // Set to true for SMTPS (port 465)
            email.isStartTLSEnabled = config.useTLS // Set to true for STARTTLS (port 587)

            // 2. Set Authentication
            email.setAuthenticator(
                DefaultAuthenticator(config.username, config.password)
            )

            // 3. Set email content
            email.setFrom(config.senderEmail, config.senderName)
            email.subject = details.subject
            email.setMsg(details.body)
            email.addTo(details.toAddress)

            // 4. Send the email
            email.send()
            println("Email sent to ${details.toAddress} successfully!")

        } catch (e: Exception) {
            println("Error sending email: ${e.message}")
            // Consider logging the error or re-throwing a custom exception
            throw e
        }
    }
}

private fun getEmailConfig(): EmailConfig {
    return EmailConfig(
        smtpHost = "smtp.gmail.com",
        smtpPort = 465,
        username = "meesam.engineer@gmail.com",
        password = "towk mxhj frdm qpwh", // **Important**: Use App Passwords for services like Gmail!
        senderEmail = "meesam.engineer@gmail.com",
        senderName = "Spring Shopping",
        useSSL = true,
        useTLS = false
    )
}

// Data class to hold your email configuration (read from application.conf/env vars)
data class EmailConfig(
    val smtpHost: String,
    val smtpPort: Int,
    val username: String ,
    val password: String , // **Important**: Use App Passwords for services like Gmail!
    val senderEmail: String,
    val senderName: String,
    val useSSL: Boolean = true,
    val useTLS: Boolean = false
)