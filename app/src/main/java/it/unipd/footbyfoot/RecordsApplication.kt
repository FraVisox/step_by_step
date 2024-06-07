package it.unipd.footbyfoot

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.database.RecordsRepository
import it.unipd.footbyfoot.database.RecordsRoomDatabase
import it.unipd.footbyfoot.database.RecordsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RecordsApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // Firebase analytics
    companion object {
        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    }

    //TODO: rimuovi scope
    private val database by lazy { RecordsRoomDatabase.getDatabase(this, applicationScope) }
    private val repository by lazy { RecordsRepository(database.goalDao(), database.workoutDao(), database.infoDao()) }

    val viewModelFactory by lazy { RecordsViewModelFactory(repository) }
}