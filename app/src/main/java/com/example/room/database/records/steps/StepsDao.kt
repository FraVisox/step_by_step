package com.example.room.database.records.steps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow



@Dao
interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(steps: Steps)

    @Query("SELECT * FROM steps_table ORDER BY date ASC")
    fun getAllStepsOrderedByDate(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date = date('now', 'localtime') ORDER BY date ASC")
    fun getTodaySteps(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date >= date('now', 'weekday 0', '-6 days') ORDER BY date ASC")
    fun getWeeklySteps(): Flow<List<Steps>>

    @Query("SELECT * FROM steps_table WHERE date >= date('now', 'start of month') ORDER BY date ASC")
    fun getMonthlySteps(): Flow<List<Steps>>



    @Query("DELETE FROM steps_table")
    suspend fun deleteAll()
}

