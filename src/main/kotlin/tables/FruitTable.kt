package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object FruitTable : UUIDTable("fruit") {
    val nama = varchar("nama", 100)
    val nama_latin = varchar("nama_latin", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val warna = varchar("manfaat" , 100)
    val musim_panen = varchar("efek_samping", 100)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}


