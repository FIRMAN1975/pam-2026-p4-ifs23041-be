package org.delcom.repositories

import org.delcom.dao.FruitDAO
import org.delcom.entities.Fruit
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.FruitTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class FruitRepository : IFruitRepository {

    override suspend fun getFruits(search: String): List<Fruit> = suspendTransaction {
        if (search.isBlank()) {
            FruitDAO.all()
                .orderBy(FruitTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            FruitDAO.find {
                FruitTable.nama.lowerCase() like keyword
            }
                .orderBy(FruitTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getFruitById(id: String): Fruit? = suspendTransaction {
        FruitDAO
            .find { FruitTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getFruitByName(name: String): Fruit? = suspendTransaction {
        FruitDAO
            .find { FruitTable.nama eq name }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addFruit(fruit: Fruit): String = suspendTransaction {
        val fruitDAO = FruitDAO.new {
            nama = fruit.nama
            nama_latin = fruit.nama_latin
            pathGambar = fruit.pathGambar
            deskripsi = fruit.deskripsi
            warna = fruit.warna
            musim_panen = fruit.musim_panen
            createdAt = fruit.createdAt
            updatedAt = fruit.updatedAt
        }

        fruitDAO.id.value.toString()
    }

    override suspend fun updateFruit(id: String, newFruit: Fruit): Boolean =
        suspendTransaction {

            val fruitDAO = FruitDAO
                .find { FruitTable.id eq UUID.fromString(id) }
                .limit(1)
                .firstOrNull()

            if (fruitDAO != null) {
                fruitDAO.nama = newFruit.nama
                fruitDAO.nama_latin = newFruit.nama_latin
                fruitDAO.pathGambar = newFruit.pathGambar
                fruitDAO.deskripsi = newFruit.deskripsi
                fruitDAO.warna = newFruit.warna
                fruitDAO.musim_panen = newFruit.musim_panen
                fruitDAO.updatedAt = newFruit.updatedAt
                true
            } else {
                false
            }
        }

    override suspend fun removeFruit(id: String): Boolean = suspendTransaction {
        val rowsDeleted = FruitTable.deleteWhere {
            FruitTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}