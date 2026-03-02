package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.delcom.tables.FruitTable
import java.util.UUID

@Serializable
data class Fruit(
    var id : String = UUID.randomUUID().toString(),
    var nama: String,
    var nama_latin:String,
    var pathGambar: String,
    var deskripsi: String,
    var warna: String,
    var musim_panen: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)


