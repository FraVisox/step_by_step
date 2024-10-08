package it.unipd.footbyfoot.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

@Entity(tableName = "point_table", primaryKeys = ["pointId","workoutId", "trackList"],
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = arrayOf("workoutId"),
            childColumns = arrayOf("workoutId"),
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("workoutId")] //Index required for the foreign key and useful for the query of the points of a certain workout
)
data class WorkoutTrackPoint(
    @ColumnInfo(name = "pointId")
    val pointId: Int,
    @ColumnInfo(name = "workoutId")
    val workoutId: Int, //External key to workout, has an index to allow fast search
    @ColumnInfo(name = "trackList")
    val trackList: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
)