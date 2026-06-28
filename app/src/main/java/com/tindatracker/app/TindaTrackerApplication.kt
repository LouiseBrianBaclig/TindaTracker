package com.tindatracker.app

import android.app.Application
import com.tindatracker.app.data.db.AppDatabase
import com.tindatracker.app.data.repository.PriceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TindaTrackerApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database   by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { PriceRepository(database) }

    override fun onCreate() {
        super.onCreate()
        // Populate sample data the very first time the app runs
        applicationScope.launch {
            repository.initializeSampleDataIfEmpty()
        }
    }
}
