package com.chalova.irina.todoapp.tasks.data.util

import androidx.room.TypeConverter
import java.time.Instant

class DateConverter {
    @TypeConverter
    fun fromTimestamp(date: Long): Instant = Instant.ofEpochMilli(date)

    @TypeConverter
    fun toTimestamp(date: Instant): Long = date.toEpochMilli()
}