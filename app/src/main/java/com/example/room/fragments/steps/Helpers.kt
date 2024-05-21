package com.example.room.fragments.steps

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Helpers {
    companion object {
        fun calculatePercentage(part: Int, total: Double): Int {
            if (total == 0.0) {
                return 0
            }
            val percentage = (part.toDouble() / total) * 100

            return if (percentage > 100) 100 else percentage.toInt()
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