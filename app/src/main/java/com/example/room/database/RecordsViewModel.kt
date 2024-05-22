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
import kotlinx.coroutines.launch


class RecordsViewModel(private val repository: RecordsRepository) : ViewModel() {

    val todaySteps: LiveData<List<Steps>> = repository.dailySteps.asLiveData()
    val todayCalories: LiveData<List<Calories>> = repository.dailyCalories.asLiveData()
    val todayDistance: LiveData<List<Distance>> = repository.dailyDistance.asLiveData()

    val weeklySteps : LiveData<List<Steps>> = repository.weeklySteps.asLiveData()
    val weeklyCalories : LiveData<List<Calories>> = repository.weeklyCalories.asLiveData()
    val weeklyDistance : LiveData<List<Distance>> = repository.weeklyDistances.asLiveData()

    val monthlySteps : LiveData<List<Steps>> = repository.monthlySteps.asLiveData()
    val monthlyCalories : LiveData<List<Calories>> = repository.monthlyCalories.asLiveData()
    val monthlyDistance : LiveData<List<Distance>> = repository.monthlyDistances.asLiveData()

    val last30UserRecords: LiveData<List<UserRecords>> = repository.getUserRecords().asLiveData()

    val allUsers : LiveData<List<User>> = repository.allUsers.asLiveData()

    val userGoal : LiveData<List<Goal>> = repository.userGoals.asLiveData()

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