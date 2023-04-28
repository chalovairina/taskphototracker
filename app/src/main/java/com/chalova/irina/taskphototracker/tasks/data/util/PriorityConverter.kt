package com.chalova.irina.taskphototracker.tasks.data.util

import androidx.room.TypeConverter

class PriorityConverter {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)
}