package com.example.room.database.distance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.room.database.steps.Steps
import kotlinx.coroutines.flow.Flow



@Dao
interface DistanceDao {

    // Questa query seleziona tutti i dati dalla tabella 'distance_table' e li ordina per data in ordine crescente
    @Query("SELECT * FROM distance_table ORDER BY date ASC")
    fun getAllDistancesOrderedByDate(): Flow<List<Distance>>

    @Query("SELECT * FROM distance_table WHERE date = date('now', 'localtime') ORDER BY date ASC")
    fun getTodayDistance(): Flow<List<Distance>>

    @Query("SELECT * FROM distance_table WHERE date >= date('now', 'weekday 0', '-6 days') ORDER BY date ASC")
    fun getWeeklyDistances(): Flow<List<Distance>>

    @Query("SELECT * FROM distance_table WHERE date >= date('now', 'start of month') ORDER BY date ASC")
    fun getMonthlyDistances(): Flow<List<Distance>>

    // Inserisce un nuovo record nella tabella. Se si verifica un conflitto (es. stesso uid), l'operazione di inserimento verrà ignorata.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(distance: Distance)

    // Cancella tutti i dati dalla tabella 'distance_table'
    @Query("DELETE FROM distance_table")
    suspend fun deleteAll()
}
