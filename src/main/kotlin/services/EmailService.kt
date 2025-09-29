package com.meesam.services

import com.meesam.domain.exceptionhandler.EmailServiceException
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

class EmailService(private val config: EmailConfig) {
    //private val logger = log

    suspend fun sendSimpleEmail(details: EmailDetails) {
        withContext(Dispatchers.IO) {
            try {
               // logger.info("Attempting to send email to ${details.toAddress}...")

                val email = SimpleEmail()

                // 1. Configure SMTP Server details
                email.hostName = config.smtpHost
                email.setSmtpPort(config.smtpPort)
                email.isSSLOnConnect = config.useSSL
                email.isStartTLSEnabled = config.useTLS

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
                //logger.info("Email sent to ${details.toAddress} successfully!")

            } catch (e: Exception) {
                //logger.error("Error sending email to ${details.toAddress}: ${e.message}", e)
                // Re-throw a service-specific exception if needed
                throw EmailServiceException("Failed to send email.", e)
            }
        }
    }
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