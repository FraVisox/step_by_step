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

    //Get for every date from the one specified, inclusive, to the one specified, inclusive, the sum of the distances of the workouts of that date
    @Query("SELECT SUM(meters) AS meters, year, dayOfYear FROM workout_table WHERE ((year = :firstYear AND dayOfYear >= :firstDayOfYear) OR (year > :firstYear)) AND ((year = :lastYear AND dayOfYear <= :lastDayOfYear) OR (year < :lastYear)) GROUP BY year, dayOfYear ORDER BY year DESC, dayOfYear DESC")
    fun getDistancesFromDateToDate(firstYear: Int, firstDayOfYear: Int, lastYear: Int, lastDayOfYear: Int): Flow<List<Distance>>

    //Get today's distance
    @Query("SELECT SUM(meters) AS meters, year, dayOfYear FROM workout_table WHERE year = :year AND dayOfYear = :dayOfYear")
    fun getTodayDistance(year: Int, dayOfYear: Int): Flow<Distance>

    /*
     * POINTS
     */
    //Get points of a workout
    @Query("SELECT * FROM point_table WHERE workoutId = :workoutId ORDER BY trackList, pointId")
    fun getWorkoutPoints(workoutId: Int): Flow<List<WorkoutTrackPoint>>

    //Insert a point of a workout
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: WorkoutTrackPoint)

    /*
     * QUERIES FOR FIREBASE
     */
    @Query("SELECT SUM(meters) FROM workout_table")
    fun getTotalDistance(): Flow<Int>

    @Query("SELECT SUM(time) FROM workout_table")
    fun getTotalTime(): Flow<Long>

    @Query("SELECT COUNT(*) FROM workout_table")
    fun countWorkout(): Flow<Int>
}