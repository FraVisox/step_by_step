package it.unipd.footbyfoot

import android.app.Application
import it.unipd.footbyfoot.database.RecordsRepository
import it.unipd.footbyfoot.database.RecordsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RecordsApplication : Application() {

    // CoroutineScope associato alla durata dell'applicazione; non è necessario cancellarlo
    // poiché verrà distrutto insieme al processo dell'applicazione.
    val applicationScope = CoroutineScope(SupervisorJob())

    // Utilizza 'by lazy' per creare il database e il repository solo quando sono necessari,
    // invece che all'avvio dell'applicazione. Questo migliora l'efficienza della gestione delle risorse.
    private val database by lazy { RecordsRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RecordsRepository(database.goalDao(), database.workoutDao()) }
}