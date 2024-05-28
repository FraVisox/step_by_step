package it.unipd.footbyfoot.database.records.steps


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "steps_table")
data class Steps(
    @PrimaryKey
    val stepId: Int,
    val userId: Int, // Chiave esterna che collega a User
    val count: Int,
    val date: Date
)