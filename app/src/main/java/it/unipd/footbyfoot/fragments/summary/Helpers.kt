package it.unipd.footbyfoot.fragments.summary

import android.widget.TextView
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.workout.Distance
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class Helpers {
    companion object {

        val defaultGoal = Goal(0,0,0,0,0.0)

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

        fun formatDateToString(date: LocalDate): String { //TODO: stringa
            val formatters = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return date.format(formatters)
        }

        fun distanceToKm(distance: Int): Double {
            return distance.toDouble()/1000
        }

        fun getGoalOfDate(goals: List<Goal>, date: LocalDate): Goal {
            for (j in goals.indices) {
                if (goals[j].year < date.year || (goals[j].year == date.year && goals[j].dayOfYear <= date.dayOfYear)) {
                    return goals[j]
                }
            }
            //Default goal
            return defaultGoal
        }

        fun getDistanceOfDate(distances: List<Distance>, date: LocalDate): Distance {
            //Get the distance of this date
            for (distanceL in distances) {
                if (LocalDate.ofYearDay(
                        distanceL.year,
                        distanceL.dayOfYear
                    ).dayOfWeek.value == date.dayOfWeek.value) {
                    return distanceL
                }
            }
            //Default distance
            return Distance(0, date.year, date.dayOfYear)
        }

        fun incrementValue(textView: TextView) {
            var value = textView.text.toString().toInt()
            value++
            textView.text = value.toString()
        }

        fun decrementValue(textView: TextView) {
            var value = textView.text.toString().toInt()
            value--
            if (value >= 0) {
                textView.text = value.toString()
            }
        }

        fun increment100Value(textView: TextView) {
            var value = textView.text.toString().toInt()
            value += 100
            textView.text = value.toString()
        }

        fun decrement100Value(textView: TextView) {
            var value = textView.text.toString().toInt()
            value -= 100
            if (value >= 0) {
                textView.text = value.toString()
            }
        }

    }

}