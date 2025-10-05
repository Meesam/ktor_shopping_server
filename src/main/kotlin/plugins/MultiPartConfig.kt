package com.meesam.plugins

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.auth.oauth2.GoogleCredentials
import io.ktor.server.application.Application
import java.io.IOException
import java.io.InputStream
import java.io.FileInputStream


fun Application.getResourcePath(resourceName: String): String? {
    val resourceURL = this::class.java.classLoader.getResource(resourceName)
    return resourceURL?.file
}

fun Application.configureFirebase() {
    try {
        val configBlock = environment.config.config("ktor.firebase")
        val bucketName = configBlock.property("bucketName").getString()
        val path = getResourcePath("spring-shopping-32af2-firebase-adminsdk-fbsvc-e2f4911b19.json").toString()
        val serviceAccount: InputStream = FileInputStream(path)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket(bucketName.toString())
            .build()

       FirebaseApp.initializeApp(options)
    } catch (e: IOException) {
        throw RuntimeException("Failed to initialize Firebase: " + e.message, e)
    }
}





