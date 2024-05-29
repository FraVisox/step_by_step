package it.unipd.footbyfoot.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "point_table", primaryKeys = ["pointId","workoutId", "trackList"])
data class WorkoutTrackPoint(
    @ColumnInfo(name = "pointId")
    val pointId: Int,
    @ColumnInfo(name = "workoutId")
    val workoutId: Int, //External key to workout
    @ColumnInfo(name = "trackList")
    val trackList: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
) : Serializable //Implements Serializable to be passed in Intents