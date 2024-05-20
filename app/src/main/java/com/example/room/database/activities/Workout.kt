package com.example.room.database.activities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workout_table")
data class Workout(
    @PrimaryKey(/*autoGenerate = true*/) @ColumnInfo(name = "workoutId")
    val workoutId: Int,
    @ColumnInfo(name = "userId")
    val userId: Int, // Chiave esterna che collega a User
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "meters")
    val meters: Int,
    @ColumnInfo(name = "date")
    val date: Date
)