package com.example.room.database.records.calories

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "calories_table")
data class Calories(
    @PrimaryKey
    val calorieId: Int,
    val userId: Int, // Chiave esterna che collega a User
    val count: Int,
    val date: Date
)