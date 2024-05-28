package it.unipd.footbyfoot.database

import it.unipd.footbyfoot.database.records.calories.Calories
import it.unipd.footbyfoot.database.records.distance.Distance
import it.unipd.footbyfoot.database.records.steps.Steps

// mi serve per collegare le varie entità
data class UserRecords(
    val userId: Int,
    val steps: Steps,
    val calories: Calories,
    val distance: Distance
)