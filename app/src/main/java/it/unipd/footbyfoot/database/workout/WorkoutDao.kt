package it.unipd.footbyfoot.database.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    /*
     * WORKOUTS
     */
    //Get all workouts, ordered by date
    @Query("SELECT * FROM workout_table ORDER BY year DESC, dayOfYear DESC, timeOfDay DESC")
    fun getAllWorkoutsOrderedByDate(): Flow<List<Workout>>

    //Insert a workout
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    //Change name of a workout
    @Query("UPDATE workout_table SET name = :name WHERE workoutId = :workoutId")
    suspend fun changeWorkoutName(workoutId: Int, name: String)

    //Delete a workout
    @Query("DELETE FROM workout_table WHERE workoutId = :id")
    suspend fun deleteWorkout(id: Int)

    /*
     * DISTANCES
     */
    //Get for every date the sum of the distances of the workouts of that date
    @Query("SELECT SUM(meters) AS meters, year, dayOfYear FROM workout_table GROUP BY year, dayOfYear ORDER BY year DESC, dayOfYear DESC")
    fun getAllDistances(): Flow<List<Distance>>

    //Get for every date from the one specified, inclusive, the sum of the distances of the workouts of that date
    @Query("SELECT SUM(meters) AS meters, year, dayOfYear FROM workout_table WHERE (year = :year AND dayOfYear >= :dayOfYear) OR (year > :year) GROUP BY year, dayOfYear ORDER BY year DESC, dayOfYear DESC")
    fun getDistancesFromDate(year: Int, dayOfYear: Int): Flow<List<Distance>>

    /*
     * POINTS
     */
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