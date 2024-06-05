package it.unipd.footbyfoot.database.workout

//Utility data class used for calculating the distances of a certain day
data class Distance(
    val meters: Int,
    val year: Int,
    val dayOfYear: Int
)