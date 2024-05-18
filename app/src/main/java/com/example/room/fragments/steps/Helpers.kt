package com.example.room.fragments.steps

import java.util.Calendar
import java.util.Date

class Helpers {
    companion object {
        fun calculatePercentage(part: Int, total: Int): Int {
            if (total == 0) {
                throw IllegalArgumentException("Total cannot be zero")
            }
            val percentage = (part.toDouble() / total.toDouble()) * 100

            return if (percentage > 100) 100 else percentage.toInt()
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