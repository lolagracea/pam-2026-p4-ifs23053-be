package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IDestinationRepository
import org.delcom.repositories.DestinationRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.DestinationService
import org.koin.dsl.module

val appModule = module {

    // =========================
    // Plant
    // =========================
    single<IPlantRepository> {
        PlantRepository()
    }

    single {
        PlantService(get())
    }

    // =========================
    // Destination (TAMBAHAN)
    // =========================
    single<IDestinationRepository> {
        DestinationRepository()
    }

    single {
        DestinationService(get())
    }

    // =========================
    // Profile
    // =========================
    single {
        ProfileService()
    }
}