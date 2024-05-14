package com.example.room

import android.app.Application
import com.example.room.database.ActivityRepository
import com.example.room.database.ActivityRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ActivityApplication : Application() {

    // CoroutineScope associato alla durata dell'applicazione; non è necessario cancellarlo
    // poiché verrà distrutto insieme al processo dell'applicazione.
    val applicationScope = CoroutineScope(SupervisorJob())

    // Utilizza 'by lazy' per creare il database e il repository solo quando sono necessari,
    // invece che all'avvio dell'applicazione. Questo migliora l'efficienza della gestione delle risorse.
    val database by lazy { ActivityRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ActivityRepository(database.userDao(), database.stepsDao(), database.caloriesDao(),database.distanceDao()) }
}