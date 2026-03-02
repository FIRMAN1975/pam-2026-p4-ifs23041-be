package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.utils.io.copyAndClose
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.FruitRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IFruitRepository
import java.io.File
import java.util.*
import io.ktor.util.cio.*

class FruitService(private val fruitRepository: IFruitRepository) {

    // ================= GET ALL =================
    suspend fun getAllFruits(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val fruits = fruitRepository.getFruits(search)

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil daftar buah",
                mapOf("fruits" to fruits)
            )
        )
    }

    // ================= GET BY ID =================
    suspend fun getFruitById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID buah tidak boleh kosong!")

        val fruit = fruitRepository.getFruitById(id)
            ?: throw AppException(404, "Data buah tidak tersedia!")

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil data buah",
                mapOf("fruit" to fruit)
            )
        )
    }

    // ================= REQUEST PARSER =================
    private suspend fun getFruitRequest(call: ApplicationCall): FruitRequest {

        val fruitReq = FruitRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->
            when (part) {

                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> fruitReq.nama = part.value.trim()
                        "nama_latin" -> fruitReq.nama_latin = part.value
                        "deskripsi" -> fruitReq.deskripsi = part.value
                        "warna" -> fruitReq.warna = part.value
                        "musim_panen" -> fruitReq.musim_panen = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/fruits/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())

                    fruitReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return fruitReq
    }

    // ================= VALIDATION =================
    private fun validateFruitRequest(fruitReq: FruitRequest) {

        val validator = ValidatorHelper(fruitReq.toMap())

        validator.required("nama", "Nama tidak boleh kosong")
        validator.required("nama_latin", "Nama latin tidak boleh kosong")
        validator.required("deskripsi", "Deskripsi tidak boleh kosong")
        validator.required("warna", "Warna tidak boleh kosong")
        validator.required("musim_panen", "Musim panen tidak boleh kosong")
        validator.required("pathGambar", "Gambar tidak boleh kosong")

        validator.validate()

        val file = File(fruitReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar buah gagal diupload!")
        }
    }

    // ================= CREATE =================
    suspend fun createFruit(call: ApplicationCall) {

        val fruitReq = getFruitRequest(call)
        validateFruitRequest(fruitReq)

        val existFruit = fruitRepository.getFruitByName(fruitReq.nama)
        if (existFruit != null) {

            File(fruitReq.pathGambar).delete()

            throw AppException(409, "Buah dengan nama ini sudah terdaftar!")
        }

        val fruitId = fruitRepository.addFruit(
            fruitReq.toEntity()
        )

        call.respond(
            DataResponse(
                "success",
                "Berhasil menambahkan data buah",
                mapOf("fruitId" to fruitId)
            )
        )
    }

    // ================= UPDATE =================
    suspend fun updateFruit(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID buah tidak boleh kosong!")

        val oldFruit = fruitRepository.getFruitById(id)
            ?: throw AppException(404, "Data buah tidak tersedia!")

        val fruitReq = getFruitRequest(call)

        if (fruitReq.pathGambar.isEmpty()) {
            fruitReq.pathGambar = oldFruit.pathGambar
        }

        validateFruitRequest(fruitReq)

        if (fruitReq.nama != oldFruit.nama) {
            val existFruit = fruitRepository.getFruitByName(fruitReq.nama)

            if (existFruit != null) {
                File(fruitReq.pathGambar).delete()
                throw AppException(409, "Buah dengan nama ini sudah terdaftar!")
            }
        }

        if (fruitReq.pathGambar != oldFruit.pathGambar) {
            File(oldFruit.pathGambar).delete()
        }

        val updated = fruitRepository.updateFruit(
            id,
            fruitReq.toEntity()
        )

        if (!updated) {
            throw AppException(400, "Gagal memperbarui data buah!")
        }

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengubah data buah",
                null
            )
        )
    }

    // ================= DELETE =================
    suspend fun deleteFruit(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID buah tidak boleh kosong!")

        val oldFruit = fruitRepository.getFruitById(id)
            ?: throw AppException(404, "Data buah tidak tersedia!")

        val deleted = fruitRepository.removeFruit(id)

        if (!deleted) {
            throw AppException(400, "Gagal menghapus data buah!")
        }

        File(oldFruit.pathGambar).delete()

        call.respond(
            DataResponse(
                "success",
                "Berhasil menghapus data buah",
                null
            )
        )
    }

    // ================= GET IMAGE =================
    suspend fun getFruitImage(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val fruit = fruitRepository.getFruitById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(fruit.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}