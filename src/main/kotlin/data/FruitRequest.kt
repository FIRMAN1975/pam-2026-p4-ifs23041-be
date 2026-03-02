package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Fruit

@Serializable
data class FruitRequest(
    var nama: String = "",
    var nama_latin: String = "",
    var deskripsi: String = "",
    var warna: String = "",
    var musim_panen: String = "",
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "nama_latin" to nama_latin,
            "deskripsi" to deskripsi,
            "warna" to warna,
            "musim_panen" to musim_panen,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Fruit {
        return Fruit(
            nama = nama,
            nama_latin = nama_latin,
            deskripsi = deskripsi,
            warna = warna,
            musim_panen = musim_panen,
            pathGambar =  pathGambar,
        )
    }

}