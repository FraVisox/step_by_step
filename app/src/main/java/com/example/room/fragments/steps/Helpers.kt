package com.example.room.fragments.steps

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Helpers {
    companion object {
        fun calculatePercentage(part: Double, total: Double): Int {
            if (total == 0.0) {
                return 0
            }
            val percentage = (part / total) * 100

            return if (percentage > 100) 100 else percentage.toInt()
        }

        fun calculateCalories(weight: Double, distance: Int): Double {
            return weight * distance * 0.9
        }
        fun calculateSteps(height: Int, distance: Double): Int {
            // Convert height from centimeters to meters
            val heightInMeters = height / 100.0
            // Distance in kilometers to meters
            val distanceInMeters = distance * 1000

            // Approximate step length
            val stepLength = 0.413 * heightInMeters

            // Calculate the number of steps
            val steps = distanceInMeters / stepLength
            return steps.toInt()
        }

        fun formatDateToString(date: Date): String {
            // Definisci il formato desiderato per la data
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Formatta la data nel formato desiderato
            return dateFormat.format(date)
        }
        fun getDayOfWeek(date: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                Calendar.SUNDAY -> "Sunday"
                else -> throw IllegalStateException("Invalid day of the week")
            }
        }
    }

}