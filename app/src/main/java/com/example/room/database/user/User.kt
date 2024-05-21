package com.example.room.database.user
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int,  // Identificativo unico dell'utente
    val userName: String, // Nome dell'utente
    val dateOfBirth: Date, // Data di nascita dell'utente
    val weight: Int,
    val height: Int
)