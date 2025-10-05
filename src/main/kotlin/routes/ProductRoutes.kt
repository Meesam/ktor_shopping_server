package com.meesam.routes

import com.meesam.domain.dto.DeleteProductFileRequest
import com.meesam.domain.dto.ProductRequest
import com.meesam.domain.dto.UpdateProductRequest
import com.meesam.services.ProductImagesService
import com.meesam.services.ProductService
import com.meesam.utills.BeanValidation
import com.meesam.utills.requireAdminOrRespond
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID
import kotlin.text.toLongOrNull

fun Route.productRoutes(service: ProductService = ProductService(), productImagesService: ProductImagesService = ProductImagesService()) {
    route("/product") {
        route("/create") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<ProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.createProduct(body)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        route("/update") {
            post {
                call.requireAdminOrRespond() ?: return@post
                val body = call.receive<UpdateProductRequest>()
                val errors = BeanValidation.errorsFor(body)
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to errors))
                    return@post
                }
                val result = service.updateProduct(body)
                call.respond(HttpStatusCode.OK, result)

            }
        }

        route("/delete") {
            delete {
                call.requireAdminOrRespond() ?: return@delete
                val productId = call.request.queryParameters["productId"]?.toLongOrNull() ?: -1
                val result = service.deleteProduct(productId)
                call.respond(HttpStatusCode.OK, result)
            }
        }

        route("/getAll") {
            get {
                val result = service.getAllProduct()
                call.respond(HttpStatusCode.OK, result)
            }
        }
         route("/uploadProductImage") {
             post {
                 val multipart = call.receiveMultipart()
                 var productId: Long? = null
                 var fileName: String? = null
                 var contentType: String? = null
                 var fileBytes: ByteArray? = null
                 multipart.forEachPart { part ->
                     when (part) {
                         is PartData.FormItem -> {
                             productId = part.value.toLong()
                         }
                        is PartData.FileItem ->{
                            fileName = part.originalFileName ?: UUID.randomUUID().toString()
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                         }
                         else -> {}
                     }
                     if(productId !=null && fileName !=null && contentType !=null && fileBytes !=null) {
                         productImagesService.uploadProductImage(productId,fileBytes, fileName, contentType)
                     }
                     part.dispose() // Important: Dispose of the part to free resources
                 }
                 call.respond(HttpStatusCode.OK)
             }
         }
        route("/deleteImage"){
            post {
                val body = call.receive<DeleteProductFileRequest>()
                productImagesService.deleteProductFile(body)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}