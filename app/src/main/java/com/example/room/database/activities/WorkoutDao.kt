package com.example.room.database.activities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Questa query seleziona tutti i dati dalla tabella 'calories_table' e li ordina per data in ordine crescente
    @Query("SELECT * FROM workout_table ORDER BY date ASC")
    fun getAllActivitiesOrderedByDate(): Flow<List<Workout>>

    /*
    @Query("SELECT * FROM workout_table WHERE date = date('now', 'localtime') ORDER BY date ASC")
    fun getTodayActivities(): Flow<List<Workout>>

    @Query("SELECT * FROM workout_table WHERE date >= date('now', 'weekday 0', '-6 days') ORDER BY date ASC")
    fun getWeeklyActivities(): Flow<List<Workout>>

    @Query("SELECT * FROM workout_table WHERE date >= date('now', 'start of month') ORDER BY date ASC")
    fun getMonthlyActivities(): Flow<List<Workout>>

     */

    /*
    @Query("SELECT * FROM workout_table WHERE activityId = :id")
    fun getPointsOfActivity(id : Int): Flow<List<WorkoutTrackPoint>>

     */

    @Query("SELECT * FROM point_table")
    fun getAllPoints(): Flow<List<WorkoutTrackPoint>>

    // Inserisce un nuovo record nella tabella. Se si verifica un conflitto (es. stesso uid), l'operazione di inserimento verrà ignorata.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activity: Workout)

    /*
    // Inserisce un nuovo record nella tabella. Se si verifica un conflitto (es. stesso uid), l'operazione di inserimento verrà ignorata.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: WorkoutTrackPoint)

     */

    // Cancella tutti i dati dalla tabella
    @Query("DELETE FROM workout_table")
    suspend fun deleteAllActivities()

    /*
    // Cancella tutti i dati dalla tabella
    @Query("DELETE FROM point_table")
    suspend fun deleteAllTracks()

     */
}