package com.example.room.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.room.database.calories.Calories
import com.example.room.database.distance.Distance
import com.example.room.database.steps.Steps
import com.example.room.database.user.User
import kotlinx.coroutines.launch


class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    val todayUserActivities: LiveData<List<UserActivityRecord>> = repository.todayActivityRecords.asLiveData()
    val weeklyUserActivities: LiveData<List<UserActivityRecord>> = repository.weeklyActivityRecords.asLiveData()
    val monthlyUserActivities: LiveData<List<UserActivityRecord>> = repository.monthlyActivityRecords.asLiveData()

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun insertStep(step: Steps) = viewModelScope.launch {
        repository.insertStep(step)
    }

    fun insertCalorie(calorie: Calories) = viewModelScope.launch {
        repository.insertCalorie(calorie)
    }

    fun insertDistance(distance: Distance) = viewModelScope.launch {
        repository.insertDistance(distance)
    }

    fun insertDayRecord(step: Steps, distance: Distance, calorie: Calories) = viewModelScope.launch {
        repository.insertDayRecord(step, distance, calorie)
    }
}

class ActivityViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}