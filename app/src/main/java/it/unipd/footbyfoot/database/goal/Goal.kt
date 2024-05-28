package it.unipd.footbyfoot.database.goal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals_table")
data class Goal(
    @PrimaryKey val userId: Int,
    val steps: Int,
    val calories: Int,
    val distance: Double,
)