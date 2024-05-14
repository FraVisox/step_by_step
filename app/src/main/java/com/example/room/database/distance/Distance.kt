package com.example.room.database.distance

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "distance_table")
data class Distance(
    @PrimaryKey
    val distanceId: Int,
    val userId: Int, // Chiave esterna che collega a User
    val count: Double,
    val date: Date
)