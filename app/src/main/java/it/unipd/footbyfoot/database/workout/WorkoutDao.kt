package it.unipd.footbyfoot.database.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    //Get all workouts
    @Query("SELECT * FROM workout_table ORDER BY date ASC")
    fun getAllWorkoutsOrderedByDate(): Flow<List<Workout>>

    //Insert a workout
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    //Delete a workout
    @Query("DELETE FROM workout_table WHERE workoutId = :id")
    suspend fun deleteWorkout(id: Int)

    //Get all the points of all workouts
    @Query("SELECT * FROM point_table")
    fun getAllPoints(): Flow<List<WorkoutTrackPoint>>

    //Insert a point of a workout
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: WorkoutTrackPoint)

    //Delete the points of a workout
    @Query("DELETE FROM point_table WHERE workoutId = :id")
    suspend fun deleteWorkoutPoints(id: Int)
}