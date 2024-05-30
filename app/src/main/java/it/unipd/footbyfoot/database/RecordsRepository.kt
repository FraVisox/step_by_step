package it.unipd.footbyfoot.database

import androidx.annotation.WorkerThread
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.goal.GoalDao
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutDao
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.database.workout.Distance

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RecordsRepository(
    private val goalDao: GoalDao,
    private val workoutDao: WorkoutDao
) {

    //All workouts and all workouts points
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsOrderedByDate()
    val allPoints: Flow<List<WorkoutTrackPoint>> = workoutDao.getAllPoints() //TODO: sarebbe meglio filtrarli per workout?

    //Sum of distances of all today's workouts
    val todayDistance: Flow<List<Distance>> = getLastXDistances(1)

    //Sum of distances of last 7 days' workouts, grouped by date
    val lastWeekDistances: Flow<List<Distance>> = getLastXDistances(LocalDate.now().dayOfWeek.value)

    //Sum of distances of last 30 days' workouts, grouped by date
    val allDistances: Flow<List<Distance>> = workoutDao.getAllDistances()

    //All goals
    val allGoals : Flow<List<Goal>> = goalDao.getAllGoals()

    val lastGoal : Flow<Goal> = goalDao.getLastGoal() //TODO: Funziona?

    //Utility function to get the sum of distances of last x days' workouts, grouped by date
    private fun getLastXDistances(x: Int): Flow<List<Distance>> {
        val dateFrom = LocalDate.now().minusDays((x-1).toLong())
        return workoutDao.getDistancesFromDate(dateFrom.year, dateFrom.dayOfYear)
    }

    //Insert a new workout, with the points associated
    @WorkerThread
    suspend fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) {
        workoutDao.insert(workout)
        var list = 0
        var index = 0
        for (p in points) {
            if (p == null) {
                list++
                index = 0
                continue
            }
            workoutDao.insert(WorkoutTrackPoint(index, workout.workoutId, list, p.latitude, p.longitude))
            index++
        }
    }

    //Delete the workout and all the points associated
    @WorkerThread
    suspend fun deleteWorkout(workoutID: Int) {
        workoutDao.deleteWorkout(workoutID)
        workoutDao.deleteWorkoutPoints(workoutID)
    }

    //This function inserts a Goal: if it is already in the database, the goal is replaced
    @WorkerThread
    suspend fun insertGoal(goal: Goal)  {
        goalDao.insert(goal)
    }
}