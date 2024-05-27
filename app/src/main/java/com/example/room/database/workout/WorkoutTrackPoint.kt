package com.example.room.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "point_table", primaryKeys = ["pointId","workoutId", "trackList"])
data class WorkoutTrackPoint(
    @ColumnInfo(name = "pointId")
    val pointId: Int,
    @ColumnInfo(name = "workoutId")
    val workoutId: Int, // Chiave esterna che collega a Activity
    @ColumnInfo(name = "trackList")
    val trackList: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
) : Serializable