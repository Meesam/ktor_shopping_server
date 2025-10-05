package com.meesam.services.firebase

import com.google.firebase.cloud.StorageClient
import java.net.URL
import java.util.UUID

class FirebaseStorageService {

    fun uploadFile(fileBytes: ByteArray?, fileName: String?, contentType: String?): String {
        if (fileBytes?.isEmpty() == true) {
            throw IllegalArgumentException("File cannot be empty.")
        }
        val bucket = StorageClient.getInstance().bucket()
        val fileName = "${UUID.randomUUID()}-${fileName}"

        val blob = bucket.create(fileName, fileBytes, contentType)

        val downloadUrl = URL("https://firebasestorage.googleapis.com/v0/b/" +
                "${bucket.name}/o/${blob.name}?alt=media")

        return downloadUrl.toString()
    }

    fun deleteFile(fileName: String): Boolean {
        val bucket = StorageClient.getInstance().bucket()
        val blob = bucket.get(fileName)
        return blob.delete()
    }
}