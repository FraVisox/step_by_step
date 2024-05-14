package com.example.room.database.calories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.room.database.distance.Distance
import kotlinx.coroutines.flow.Flow



@Dao
interface CaloriesDao {

    // Questa query seleziona tutti i dati dalla tabella 'calories_table' e li ordina per data in ordine crescente
    @Query("SELECT * FROM calories_table ORDER BY date ASC")
    fun getAllCaloriesOrderedByDate(): Flow<List<Calories>>

    @Query("SELECT * FROM calories_table WHERE date = date('now', 'localtime') ORDER BY date ASC")
    fun getTodayCalories(): Flow<List<Calories>>


    @Query("SELECT * FROM calories_table WHERE date >= date('now', 'weekday 0', '-6 days') ORDER BY date ASC")
    fun getWeeklyCalories(): Flow<List<Calories>>

    @Query("SELECT * FROM calories_table WHERE date >= date('now', 'start of month') ORDER BY date ASC")
    fun getMonthlyCalories(): Flow<List<Calories>>

    // Inserisce un nuovo record nella tabella. Se si verifica un conflitto (es. stesso uid), l'operazione di inserimento verrà ignorata.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calories: Calories)

    // Cancella tutti i dati dalla tabella 'calories_table'
    @Query("DELETE FROM calories_table")
    suspend fun deleteAll()
}