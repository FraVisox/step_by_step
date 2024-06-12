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

    //Distances
    val allDistances : LiveData<List<Distance>> = repository.allDistances.asLiveData()

    //Goals and info
    val allGoals : LiveData<List<Goal>> = repository.allGoals.asLiveData()
    val allInfo : LiveData<List<UserInfo>> = repository.allInfo.asLiveData()

    //Utility function to get the sum of distances of this week's workouts, grouped by date
    fun getThisWeekDistances(): LiveData<List<Distance>> {
        return repository.getThisWeekDistances().asLiveData()
    }

    fun getTodayDistance(): LiveData<Distance> {
        return repository.getTodayDistance().asLiveData()
    }

    //Get points of a workout
    fun getWorkoutPoints(workoutId: Int): LiveData<List<WorkoutTrackPoint>>? {
        if (workoutId != invalidWorkoutID) {
            //No need to put a trace, as it is not an insertion or deletion or change
            return repository.getWorkoutPoints(workoutId).asLiveData()
        }
        return null
    }

    //Insert a new goal for a day (if there is already one, replace it)
    fun insertGoal(goal: Goal) {
        val goalTrace = Firebase.performance.newTrace(RecordsApplication.insertGoalTrace)
        goalTrace.start()
        viewModelScope.launch {
            repository.insertGoal(goal)
            goalTrace.stop()
        }
    }

    //Insert a new user info for a day (if there is already one, replace it)
    fun insertInfo(info: UserInfo) {
        val infoTrace = Firebase.performance.newTrace(RecordsApplication.insertInfoTrace)
        infoTrace.start()
        viewModelScope.launch {
            repository.insertInfo(info)
            infoTrace.stop()
        }
    }

    //Insert a new workout, with the corresponding points
    fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) {
        val workoutTrace = Firebase.performance.newTrace(RecordsApplication.insertWorkoutTrace)
        workoutTrace.start()
        viewModelScope.launch {
            repository.insertWorkout(workout, points)
            workoutTrace.stop()
        }
    }

    //Change the name of a workout
    fun changeWorkoutName(workoutId: Int, name: String) {
        if (workoutId != invalidWorkoutID) {
            val changeWorkout = Firebase.performance.newTrace(RecordsApplication.changeWorkoutTrace)
            changeWorkout.start()
            viewModelScope.launch {
                repository.changeWorkoutName(workoutId, name)
                changeWorkout.stop()
            }
        }
    }

    //Delete the workout, with the points associated
    fun deleteWorkout(workoutId: Int) {
        if (workoutId != invalidWorkoutID) {
            val deleteWorkout = Firebase.performance.newTrace(RecordsApplication.deleteWorkoutTrace)
            deleteWorkout.start()
            viewModelScope.launch {
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