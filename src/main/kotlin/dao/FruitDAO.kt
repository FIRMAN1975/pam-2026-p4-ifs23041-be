package org.delcom.dao

import org.delcom.tables.FruitTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID


class FruitDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, FruitDAO>(FruitTable)

    var nama by FruitTable.nama
    var nama_latin by FruitTable.nama_latin
    var pathGambar by FruitTable.pathGambar
    var deskripsi by FruitTable.deskripsi
    var warna by FruitTable.warna
    var musim_panen by FruitTable.musim_panen
    var createdAt by FruitTable.createdAt
    var updatedAt by FruitTable.updatedAt
}