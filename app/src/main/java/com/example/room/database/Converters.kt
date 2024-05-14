package com.example.room.database

import androidx.room.TypeConverter
import java.util.Date


// sono stato costretto ad aggiungerlo altrimenti non andava
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

}