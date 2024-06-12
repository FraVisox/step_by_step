package it.unipd.footbyfoot.fragments

import android.content.Context
import android.widget.TextView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.goals.GoalsFragment
import it.unipd.footbyfoot.fragments.settings.SettingsFragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Helpers {
    //Default goal and info used for workouts previous than the first setting of settings or goals
    val defaultGoal = Goal(0,0,GoalsFragment.defaultGoal,GoalsFragment.defaultGoal,GoalsFragment.defaultGoal)
    private val defaultInfo = UserInfo(0,0, SettingsFragment.defaultHeight, SettingsFragment.defaultWeight)

    /*
     * SUMMARY FRAGMENT
     */
    //Calculate the percentage of the part on the total
    fun calculatePercentage(part: Double, total: Double): Int {
        if (total == 0.0) {
            return 0
        }
        val percentage = (part / total) * 100
        return if (percentage > 100) 100 else percentage.toInt()
    }

    //Calculate calories based on weight and distance
    fun calculateCalories(weight: Int, distance: Int): Double {
        val result = weight * (distance.toDouble() / 1000) * 0.9
        return "%.1f".format(Locale.US, result).toDouble()
    }

    //Calculate steps based on height and distance
    fun calculateSteps(height: Int, distance: Int): Int {
        if (height == 0) {
            return 0
        }
        // Convert height from centimeters to meters
        val heightInMeters = height.toDouble() / 100.0

        // Approximate step length
        val stepLength = 0.413 * heightInMeters

        // Calculate the number of steps
        val steps = distance / stepLength
        return steps.toInt()
    }

    /*
     * FORMATTING OF DATES AND TIME
     */

    //Formats only the date
    fun formatDateToString(context: Context, date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(context.getString(R.string.date_format_pattern), Locale.getDefault())
        return formatter.format(date)
    }

    //Formats date and time
    fun formatDateTimeToString(context: Context, date: LocalDate, timeOfDay: String): String {
        return "${formatDateToString(context, date)}\n$timeOfDay"
    }

    //Formats time given a DateTime
    fun formatTimeToString(context: Context, date: LocalDateTime): String {
        return formatTimeToString(context, date.hour, date.minute)
    }

    //Formats time given hour and minute
    fun formatTimeToString(context: Context, hours: Int, minutes: Int): String {
        return context.getString(R.string.hour_format_pattern, hours, minutes)
    }

    //Formats duration (with seconds)
    fun formatDurationToString(context: Context, hours: Int, minutes: Int, seconds: Int): String {
        return context.getString(R.string.time_format, hours, minutes, seconds)
    }

    /*
     * CALCULATING OF TIME
     */

    //Calculate the number of seconds, given hour, minutes and seconds
    fun getSecondsFromTime(hours: Int, minutes: Int, seconds: Int): Long {
        return (seconds+minutes*60+hours*3600).toLong()
    }

    //Obtain seconds from total time
    fun getSeconds(total: Long): Int {
        return (total%60).toInt()
    }

    //Obtain minutes from total time
    fun getMinutes(total: Long): Int {
        return ((total/60)%60).toInt()
    }

    //Obtain hours from total time
    fun getHours(total: Long): Int {
        return (total/3600).toInt()
    }

    /*
     * GET GOAL, INFO AND DISTANCE OF DATE
     */

    //Get the goal for the current date
    fun getGoalOfDate(goals: List<Goal>, date: LocalDate): Goal {
        for (j in goals.indices) {
            if (goals[j].year < date.year || (goals[j].year == date.year && goals[j].dayOfYear <= date.dayOfYear)) {
                return goals[j]
            }
        }
        //Default goal
        return defaultGoal
    }

    //Get the goal for the current date
    fun getInfoOfDate(info: List<UserInfo>, date: LocalDate): UserInfo {
        for (j in info.indices) {
            if (info[j].year < date.year || (info[j].year == date.year && info[j].dayOfYear <= date.dayOfYear)) {
                return info[j]
            }
        }
        //Default goal
        return defaultInfo
    }

    //Get the distance of a date in a list of weekly dates
    fun getDistanceMetersOfDateInWeek(distances: List<Distance>, date: LocalDate): Int {
        //Get the distance of this date
        for (distanceL in distances) {
            if (LocalDate.ofYearDay(
                    distanceL.year,
                    distanceL.dayOfYear
                ).dayOfWeek.value == date.dayOfWeek.value) {
                return distanceL.meters
            }
        }
        //Default distance
        return 0
    }

    /*
     * INCREMENT AND DECREMENT VALUES OF TEXT VIEWS
     */

    fun incrementValue(textView: TextView?) {
        if (textView != null) {
            var value = textView.text.toString().toInt()
            value++
            textView.text = value.toString()
        }
    }

    fun decrementValue(textView: TextView?) {
        if (textView != null) {
            var value = textView.text.toString().toInt()
            value--
            if (value >= 0) {
                textView.text = value.toString()
            }
        }
    }

    fun increment100Value(textView: TextView?) {
        if (textView != null) {
            var value = textView.text.toString().toInt()
            value += 100
            textView.text = value.toString()
        }
    }

    fun decrement100Value(textView: TextView?) {
        if (textView != null) {
            var value = textView.text.toString().toInt()
            value -= 100
            if (value >= 0) {
                textView.text = value.toString()
            }
        }
    }
}