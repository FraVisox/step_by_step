package it.unipd.footbyfoot.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.records.distance.Distance
import it.unipd.footbyfoot.database.user.User
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    companion object {
        const val invalidID = -1
    }

    val lastDistance: LiveData<List<Distance>> = repository.lastDistance.asLiveData()
    val last7Distances : LiveData<List<Distance>> = repository.last7Distances.asLiveData()
    val last30Distances : LiveData<List<Distance>> = repository.last30Distances.asLiveData()

    val last30UserRecords: LiveData<List<UserRecords>> = repository.getUserRecords().asLiveData()

    val allUsers : LiveData<List<User>> = repository.allUsers.asLiveData()
    val userGoal : LiveData<List<Goal>> = repository.userGoals.asLiveData()

    val allWorkouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()
    val allPoints: LiveData<List<WorkoutTrackPoint>> = repository.allPoints.asLiveData()

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun insertGoal(goal: Goal) = viewModelScope.launch {
        repository.insertGoal(goal)
    }

    fun updateGoal(goal: Goal) = viewModelScope.launch {
        repository.updateGoal(goal)
    }

    fun updateUserInfo(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    //Insert a new workout, with the corresponding points
    fun insertWorkout(workout: Workout, points: MutableList<LatLng?>) =
        viewModelScope.launch {
            repository.insertWorkout(workout, points)
        }

    //Delete the workout, with the points associated
    fun deleteWorkout(workoutId: Int) {
        if (workoutId != invalidID) {
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