package it.unipd.footbyfoot.fragments.summary

import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

        fun calculateCalories(weight: Int, distance: Int): Double {
            val result = weight.toDouble() * distanceToKm(distance) * 0.9
            return "%.1f".format(Locale.US, result).toDouble()
        }
        fun calculateSteps(height: Int, distance: Int): Int {
            // Convert height from centimeters to meters
            val heightInMeters = height.toDouble() / 100.0

            // Approximate step length
            val stepLength = 0.413 * heightInMeters

            // Calculate the number of steps
            val steps = distance / stepLength
            return steps.toInt()
        }

        fun formatDateToString(year: Int, dayOfYear: Int): String {
            val formatters = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return LocalDate.ofYearDay(year, dayOfYear).format(formatters)
        }

        fun distanceToKm(distance: Int): Double {
            return distance.toDouble()/1000
        }

    }

}