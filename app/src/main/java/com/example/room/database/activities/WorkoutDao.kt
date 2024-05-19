package com.example.room.database.activities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao { //TODO: replace o ignore?

    //WORKOUTS
    @Query("SELECT * FROM workout_table ORDER BY date ASC")
    fun getAllActivitiesOrderedByDate(): Flow<List<Workout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Query("DELETE FROM workout_table")
    suspend fun deleteAllActivities()

    @Query("DELETE FROM workout_table WHERE workoutId = :id")
    fun deleteWorkout(id: Int)

    //POINTS OF WORKOUTS
    @Query("SELECT * FROM point_table WHERE workoutId = :id")
    fun getPointsOfWorkout(id : Int): Flow<List<WorkoutTrackPoint>>

    @Query("SELECT * FROM point_table")
    fun getAllPoints(): Flow<List<WorkoutTrackPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: WorkoutTrackPoint)

    @Query("DELETE FROM point_table")
    suspend fun deleteAllPoints()

    @Query("DELETE FROM point_table WHERE workoutId = :id")
    fun deleteWorkoutPoints(id: Int)
}