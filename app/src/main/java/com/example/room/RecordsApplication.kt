package com.example.room

import android.app.Application
import android.util.Log
import androidx.activity.viewModels
import com.example.room.database.RecordsRepository
import com.example.room.database.RecordsRoomDatabase
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RecordsApplication : Application() {

    // CoroutineScope associato alla durata dell'applicazione; non è necessario cancellarlo
    // poiché verrà distrutto insieme al processo dell'applicazione.
    val applicationScope = CoroutineScope(SupervisorJob())

    // Utilizza 'by lazy' per creare il database e il repository solo quando sono necessari,
    // invece che all'avvio dell'applicazione. Questo migliora l'efficienza della gestione delle risorse.
    private val database by lazy { RecordsRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RecordsRepository(database.userDao(), database.stepsDao(), database.caloriesDao(),database.distanceDao(), database.goalDao(), database.workoutDao()) }

    var workoutId = 1
}