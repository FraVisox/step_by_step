package it.unipd.footbyfoot.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.workout.Distance
import kotlinx.coroutines.launch


class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    companion object {
        const val invalidWorkoutID = -1
    }

    //Firebase metrics
    val totalDistance: LiveData<Int> = repository.totalDistance.asLiveData()
    val totalTime: LiveData<Long> = repository.totalTime.asLiveData()
    val countWorkout: LiveData<Int> = repository.countWorkout.asLiveData()

    //Workouts
    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()
    val allPoints: LiveData<List<WorkoutTrackPoint>> = repository.allPoints.asLiveData()

    //Distances
    val todayDistance: LiveData<Distance> = repository.todayDistance.asLiveData()
    val lastWeekDistances : LiveData<List<Distance>> = repository.lastWeekDistances.asLiveData()
    val allDistances : LiveData<List<Distance>> = repository.allDistances.asLiveData()

    //Goals and info
    val allGoals : LiveData<List<Goal>> = repository.allGoals.asLiveData()
    val allInfo : LiveData<List<UserInfo>> = repository.allInfo.asLiveData()

    //Insert a new goal for a day (if there is already one, replace it)
    fun insertGoal(goal: Goal) = viewModelScope.launch {
        val goalTrace = Firebase.performance.newTrace("insert_goal")
        goalTrace.start()
        repository.insertGoal(goal)
        goalTrace.stop()
    }

    //Insert a new user info for a day (if there is already one, replace it)
    fun insertInfo(info: UserInfo) = viewModelScope.launch {
        val infoTrace = Firebase.performance.newTrace("insert_info")
        infoTrace.start()
        repository.insertInfo(info)
        infoTrace.stop()
    }

    //Insert a new workout, with the corresponding points
    fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) =
        viewModelScope.launch {
            val workoutTrace = Firebase.performance.newTrace("insert_workout")
            workoutTrace.start()
            repository.insertWorkout(workout, points)
            workoutTrace.stop()
        }

    //Change the name of a workout
    fun changeWorkoutName(workoutId: Int, name: String) {
        if (workoutId != invalidWorkoutID) {
            viewModelScope.launch {
                val changeWorkout = Firebase.performance.newTrace("change_workout")
                changeWorkout.start()
                repository.changeWorkoutName(workoutId, name)
                changeWorkout.stop()
            }
        }
    }

    //Delete the workout, with the points associated
    fun deleteWorkout(workoutId: Int) {
        if (workoutId != invalidWorkoutID) {
            viewModelScope.launch {
                val deleteWorkout = Firebase.performance.newTrace("delete_workout")
                deleteWorkout.start()
                repository.deleteWorkout(workoutId)
                deleteWorkout.stop()
            }
        }
    }
}

class RecordsViewModelFactory(private val repository: RecordsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}