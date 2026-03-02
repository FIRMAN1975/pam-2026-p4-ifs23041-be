package org.delcom.module

import org.delcom.repositories.FruitRepository
import org.delcom.repositories.IFruitRepository
import org.delcom.services.FruitService
import org.delcom.services.ProfileService
import org.koin.dsl.module


val appModule = module {
    // Fruit Repository
    single<IFruitRepository> {
        FruitRepository()
    }

    // Fruit Service
    single {
        FruitService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}