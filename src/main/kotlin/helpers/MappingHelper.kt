package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.FruitDAO
import org.delcom.entities.Fruit
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: FruitDAO) = Fruit(
    dao.id.value.toString(),
    dao.nama,
    dao.nama_latin,
    dao.pathGambar,
    dao.deskripsi,
    dao.warna,
    dao.musim_panen,
    dao.createdAt,
    dao.updatedAt
)
