package it.unipd.footbyfoot.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class Workout(
    @PrimaryKey @ColumnInfo(name = "workoutId")
    val workoutId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "meters")
    val meters: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "dayOfYear")
    val dayOfYear: Int,
    @ColumnInfo(name = "timeOfDay")
    val timeOfDay: String
)