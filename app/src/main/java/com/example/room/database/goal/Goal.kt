package com.example.room.database.goal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.room.database.user.User

@Entity(tableName = "goals_table")
data class Goal(
    @PrimaryKey val userId: Int,
    val steps: Int,
    val calories: Int,
    val distance: Double,
)