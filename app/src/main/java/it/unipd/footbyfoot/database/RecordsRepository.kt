package it.unipd.footbyfoot.database

import androidx.annotation.WorkerThread
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.goal.GoalDao
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutDao
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.userinfo.UserInfoDao
import it.unipd.footbyfoot.database.workout.Distance

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RecordsRepository(
    private val goalDao: GoalDao,
    private val workoutDao: WorkoutDao,
    private val infoDao: UserInfoDao
) {
    //Metrics used for firebase
    val totalDistance: Flow<Int> = workoutDao.getTotalDistance()
    val totalTime: Flow<Long> = workoutDao.getTotalTime()
    val countWorkout: Flow<Int> = workoutDao.countWorkout()

    //All workouts
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsOrderedByDate()

    //Sum of distances of all workouts, grouped by date
    val allDistances: Flow<List<Distance>> = workoutDao.getAllDistances()

    //All goals
    val allGoals : Flow<List<Goal>> = goalDao.getAllGoals()

    //All user info
    val allInfo : Flow<List<UserInfo>> = infoDao.getAllInfo()

    //Function to get the sum of distances of this week's workouts, grouped by date
    fun getThisWeekDistances(): Flow<List<Distance>> {
        val now = LocalDate.now().dayOfWeek.value
        val dateFrom = LocalDate.now().minusDays((now-1).toLong())
        val dateTo = dateFrom.plusDays(6L)
        return workoutDao.getDistancesFromDateToDate(dateFrom.year, dateFrom.dayOfYear, dateTo.year, dateTo.dayOfYear)
    }

    fun getTodayDistance(): Flow<Distance> {
        return workoutDao.getTodayDistance(LocalDate.now().year, LocalDate.now().dayOfYear)
    }

    //Insert a new workout, with the points associated
    @WorkerThread
    suspend fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) {
        workoutDao.insert(workout)
        var list = 0
        var index = 0
        var indicator = 0
        for (p in points) {
            if (p == null) { //Null is the value that separates two lists
                list++
                index = 0
                continue
            }
            workoutDao.insert(WorkoutTrackPoint(index, workout.workoutId, list, p.latitude, p.longitude))
            index++
            indicator++
        }
    }

    //Change the name of the workout
    @WorkerThread
    suspend fun changeWorkoutName(workoutId: Int, name: String) {
        workoutDao.changeWorkoutName(workoutId, name)
    }

    //Delete the workout and all the points associated (it is done on cascade)
    @WorkerThread
    suspend fun deleteWorkout(workoutID: Int) {
        workoutDao.deleteWorkout(workoutID)
    }

    //Insert a Goal: if a goal with the same date is already in the database, the goal is replaced
    @WorkerThread
    suspend fun insertGoal(goal: Goal)  {
        goalDao.insert(goal)
    }

    //Insert a UserInfo: if an info with the same date is already in the database, the info is replaced
    @WorkerThread
    suspend fun insertInfo(info: UserInfo)  {
        infoDao.insert(info)
    }

    //Get points of a workout
    fun getWorkoutPoints(workoutID: Int): Flow<List<WorkoutTrackPoint>> {
        return workoutDao.getWorkoutPoints(workoutID)
    }
}