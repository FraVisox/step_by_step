package com.example.room.database

import com.example.room.database.calories.Calories
import com.example.room.database.distance.Distance
import com.example.room.database.steps.Steps
import com.example.room.database.user.User

// mi serve per collegare le varie entità
data class UserActivityRecord(
    val user: User,
    val steps: Steps,
    val calories: Calories,
    val distance: Distance
)