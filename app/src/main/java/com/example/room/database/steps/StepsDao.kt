package com.example.room.database.steps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow



@Dao
interface StepsDao {

    // La query seleziona tutti i record dalla tabella 'steps_table' ordinandoli in base alla data in modo crescente
    @Query("SELECT * FROM steps_table ORDER BY date ASC")
    fun getAllStepsOrderedByDate(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date = date('now', 'localtime') ORDER BY date ASC")
    fun getTodaySteps(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date >= date('now', 'weekday 0', '-6 days') ORDER BY date ASC")
    fun getWeeklySteps(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date >= date('now', 'start of month') ORDER BY date ASC")
    fun getMonthlySteps(): Flow<List<Steps>>
    // Inserisce un nuovo record nella tabella. Se si verifica un conflitto (es. stesso uid), l'operazione di inserimento verrà ignorata.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(steps: Steps)

    // Cancella tutti i dati dalla tabella 'steps_table'
    @Query("DELETE FROM steps_table")
    suspend fun deleteAll()
}

