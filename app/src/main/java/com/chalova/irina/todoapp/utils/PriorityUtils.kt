package com.chalova.irina.todoapp.utils

import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.tasks.data.util.Priority

object PriorityUtils {

    const val PRIORITY_POSITION_HIGH = 0
    const val PRIORITY_POSITION_MEDIUM = 1
    const val PRIORITY_POSITION_LOW = 2

    const val PRIORITY_COLOR_HIGH = R.color.highPriorityColor
    const val PRIORITY_COLOR_MEDIUM = R.color.mediumPriorityColor
    const val PRIORITY_COLOR_LOW = R.color.lowPriorityColor

    const val PRIORITY_NAME_HIGH = R.string.priority_high
    const val PRIORITY_NAME_MEDIUM = R.string.priority_medium
    const val PRIORITY_NAME_LOW = R.string.priority_low

    fun getColorByPriorityPosition(position: Int): Int {
        return when(position) {
            PRIORITY_POSITION_HIGH -> PRIORITY_COLOR_HIGH
            PRIORITY_POSITION_MEDIUM -> PRIORITY_COLOR_MEDIUM
            else -> PRIORITY_COLOR_LOW
        }
    }

    fun getPriorityByPriorityPosition(position: Int): Priority {
        return when(position) {
            PRIORITY_POSITION_HIGH -> Priority.HIGH
            PRIORITY_POSITION_MEDIUM -> Priority.MEDIUM
            else -> Priority.LOW
        }
    }

    fun getColorByPriority(priority: Priority): Int {
        return when(priority) {
            Priority.HIGH -> PRIORITY_COLOR_HIGH
            Priority.MEDIUM -> PRIORITY_COLOR_MEDIUM
            Priority.LOW -> PRIORITY_COLOR_LOW
        }
    }

}