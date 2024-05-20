package com.example.room.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "point_table", primaryKeys = ["pointId","workoutId"])
data class WorkoutTrackPoint(
    @ColumnInfo(name = "pointId")
    val pointId: Int,
    @ColumnInfo(name = "workoutId")
    val workoutId: Int, // Chiave esterna che collega a Activity
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
)