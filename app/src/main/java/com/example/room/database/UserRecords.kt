package com.example.room.database

import com.example.room.database.records.calories.Calories
import com.example.room.database.records.distance.Distance
import com.example.room.database.records.steps.Steps

// mi serve per collegare le varie entità
data class UserRecords(
    val userId: Int,
    val steps: Steps,
    val calories: Calories,
    val distance: Distance
)