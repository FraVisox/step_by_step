package com.example.room.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.room.database.goal.Goal
import com.example.room.database.records.calories.Calories
import com.example.room.database.records.distance.Distance
import com.example.room.database.records.steps.Steps
import com.example.room.database.user.User
import com.example.room.database.workout.Workout
import com.example.room.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    companion object {
        val positionPlaceholder = LatLng(-1.0, -1.0)
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

    fun insertSteps(step: Steps) = viewModelScope.launch {
        repository.insertStep(step)
    }

    fun insertCalories(calorie: Calories) = viewModelScope.launch {
        repository.insertCalorie(calorie)
    }

    fun insertDistance(distance: Distance) = viewModelScope.launch {
        repository.insertDistance(distance)
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

    fun insertTodayRecords(step: Steps, distance: Distance, calorie: Calories) = viewModelScope.launch {
        repository.insertDayRecord(step, distance, calorie)
    }

    fun insertWorkout(workout: Workout, points: MutableList<LatLng>) =
        viewModelScope.launch {
            repository.insertWorkout(workout, points)
        }


    fun getActivityPoints(workout: Workout): LiveData<List<WorkoutTrackPoint>> {
        return repository.getWorkoutPoints(workout).asLiveData()
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