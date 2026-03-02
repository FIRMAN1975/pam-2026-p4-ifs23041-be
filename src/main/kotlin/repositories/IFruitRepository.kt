package org.delcom.repositories

import org.delcom.entities.Fruit

interface IFruitRepository {
    suspend fun getFruits(search: String): List<Fruit>
    suspend fun getFruitById(id: String): Fruit?
    suspend fun getFruitByName(name: String): Fruit?
    suspend fun addFruit(Fruit: Fruit) : String
    suspend fun updateFruit(id: String, newPlant: Fruit): Boolean
    suspend fun removeFruit(id: String): Boolean

}
