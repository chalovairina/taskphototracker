package com.chari.ic.todoapp.utils

import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority

object PriorityUtils {
    const val PRIORITY_POSITION_HIGH = 0
    const val PRIORITY_POSITION_MEDIUM = 1
    const val PRIORITY_POSITION_LOW = 2

    const val PRIORITY_COLOR_HIGH = R.color.red
    const val PRIORITY_COLOR_MEDIUM = R.color.yellow
    const val PRIORITY_COLOR_LOW = R.color.green

    const val PRIORITY_NAME_HIGH = "High Priority"
    const val PRIORITY_NAME_MEDIUM = "Medium Priority"
    const val PRIORITY_NAME_LOW = "Low Priority"

    fun getColorByPriorityPosition(position: Int): Int {
        return when(position) {
            PRIORITY_POSITION_HIGH -> PRIORITY_COLOR_HIGH
            PRIORITY_POSITION_MEDIUM -> PRIORITY_COLOR_MEDIUM
            PRIORITY_POSITION_LOW -> PRIORITY_COLOR_LOW
            else -> PRIORITY_COLOR_LOW
        }
    }

    fun getColorByPriority(priority: Priority): Int {
        return when(priority) {
            Priority.HIGH -> PRIORITY_COLOR_HIGH
            Priority.MEDIUM -> PRIORITY_COLOR_MEDIUM
            Priority.LOW -> PRIORITY_COLOR_LOW
        }
    }

    fun getPriorityByName(priority: String): Priority {
        return when (priority) {
            PRIORITY_NAME_HIGH -> Priority.HIGH
            PRIORITY_NAME_MEDIUM -> Priority.MEDIUM
            PRIORITY_NAME_LOW -> Priority.LOW
            else -> Priority.LOW
        }
    }

    fun getPositionByPriority(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> PRIORITY_POSITION_HIGH
            Priority.MEDIUM -> PRIORITY_POSITION_MEDIUM
            Priority.LOW -> PRIORITY_POSITION_LOW
        }
    }

}