package it.unipd.footbyfoot.database.goal

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "goals_table", primaryKeys = ["year","dayOfYear"])
data class Goal(
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "dayOfYear")
    val dayOfYear: Int,
    @ColumnInfo(name = "steps")
    val steps: Int,
    @ColumnInfo(name = "calories")
    val calories: Int,
    @ColumnInfo(name = "distance")
    val distance: Double
)