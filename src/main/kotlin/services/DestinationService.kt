package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DestinationRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDestinationRepository
import java.io.File
import java.util.*

class DestinationService(private val destinationRepository: IDestinationRepository) {

    // =============================
    // GET ALL
    // =============================
    suspend fun getAllDestinations(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val destinations = destinationRepository.getDestinations(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar destinasi",
            mapOf(Pair("destinations", destinations))
        )
        call.respond(response)
    }

    // =============================
    // GET BY ID
    // =============================
    suspend fun getDestinationById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID destinasi tidak boleh kosong!")

        val destination = destinationRepository.getDestinationById(id)
            ?: throw AppException(404, "Data destinasi tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data destinasi",
            mapOf(Pair("destination", destination))
        )
        call.respond(response)
    }

    // =============================
    // MULTIPART REQUEST
    // =============================
    private suspend fun getDestinationRequest(call: ApplicationCall): DestinationRequest {
        val destinationReq = DestinationRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->
            when (part) {

                is PartData.FormItem -> {
                    when (part.name) {
                        "name" -> destinationReq.name = part.value.trim()
                        "country" -> destinationReq.country = part.value.trim()
                        "city" -> destinationReq.city = part.value.trim()
                        "description" -> destinationReq.description = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/destinations/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    destinationReq.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return destinationReq
    }

    // =============================
    // VALIDATION
    // =============================
    private fun validateDestinationRequest(destinationReq: DestinationRequest) {
        val validator = ValidatorHelper(destinationReq.toMap())

        validator.required("name", "Nama destinasi tidak boleh kosong")
        validator.required("country", "Country tidak boleh kosong")
        validator.required("pathGambar", "Gambar tidak boleh kosong")

        validator.validate()

        val file = File(destinationReq.pathGambar ?: "")
        if (!file.exists()) {
            throw AppException(400, "Gambar destinasi gagal diupload!")
        }
    }

    // =============================
    // CREATE
    // =============================
    suspend fun createDestination(call: ApplicationCall) {

        val destinationReq = getDestinationRequest(call)

        validateDestinationRequest(destinationReq)

        val exist = destinationRepository.getDestinationByName(destinationReq.name)
        if (exist != null) {
            val tmpFile = File(destinationReq.pathGambar!!)
            if (tmpFile.exists()) tmpFile.delete()

            throw AppException(409, "Destinasi dengan nama ini sudah terdaftar!")
        }

        val destinationId = destinationRepository.addDestination(
            destinationReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data destinasi",
            mapOf(Pair("destinationId", destinationId))
        )

        call.respond(response)
    }

    // =============================
    // UPDATE
    // =============================
    suspend fun updateDestination(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID destinasi tidak boleh kosong!")

        val oldDestination = destinationRepository.getDestinationById(id)
            ?: throw AppException(404, "Data destinasi tidak tersedia!")

        val destinationReq = getDestinationRequest(call)

        if (destinationReq.pathGambar.isNullOrEmpty()) {
            destinationReq.pathGambar = oldDestination.pathGambar
        }

        validateDestinationRequest(destinationReq)

        if (destinationReq.name != oldDestination.name) {
            val exist = destinationRepository.getDestinationByName(destinationReq.name)
            if (exist != null) {
                throw AppException(409, "Destinasi dengan nama ini sudah terdaftar!")
            }
        }

        if (destinationReq.pathGambar != oldDestination.pathGambar) {
            val oldFile = File(oldDestination.pathGambar ?: "")
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = destinationRepository.updateDestination(
            id,
            destinationReq.toEntity()
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data destinasi!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data destinasi",
            null
        )

        call.respond(response)
    }

    // =============================
    // DELETE
    // =============================
    suspend fun deleteDestination(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID destinasi tidak boleh kosong!")

        val oldDestination = destinationRepository.getDestinationById(id)
            ?: throw AppException(404, "Data destinasi tidak tersedia!")

        val oldFile = File(oldDestination.pathGambar ?: "")

        val isDeleted = destinationRepository.removeDestination(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data destinasi!")
        }

        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data destinasi",
            null
        )

        call.respond(response)
    }

    // =============================
    // GET IMAGE
    // =============================
    suspend fun getDestinationImage(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val destination = destinationRepository.getDestinationById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(destination.pathGambar ?: "")
        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}