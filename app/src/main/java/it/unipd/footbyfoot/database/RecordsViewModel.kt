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
import it.unipd.footbyfoot.database.workout.Distance
import kotlinx.coroutines.launch


class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    companion object {
        const val invalidWorkoutID = -1
    }

    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()
    val allPoints: LiveData<List<WorkoutTrackPoint>> = repository.allPoints.asLiveData()

    val todayDistance: LiveData<List<Distance>> = repository.todayDistance.asLiveData()
    val lastWeekDistances : LiveData<List<Distance>> = repository.lastWeekDistances.asLiveData()
    val allDistances : LiveData<List<Distance>> = repository.allDistances.asLiveData()

    val allGoals : LiveData<List<Goal>> = repository.allGoals.asLiveData()

    //Insert a new goal for a day (if there is already one, replace it)
    fun insertGoal(goal: Goal) = viewModelScope.launch {
        repository.insertGoal(goal)
    }

    //Insert a new workout, with the corresponding points
    fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) =
        viewModelScope.launch {
            repository.insertWorkout(workout, points)
        }

    //Change the name of a workout
    fun changeWorkoutName(workoutId: Int, name: String) {
        if (workoutId != invalidWorkoutID) {
            viewModelScope.launch {
                repository.changeWorkoutName(workoutId, name)
            }
        }
    }

    //Delete the workout, with the points associated
    fun deleteWorkout(workoutId: Int) {
        if (workoutId != invalidWorkoutID) {
            viewModelScope.launch {
                repository.deleteWorkout(workoutId)
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