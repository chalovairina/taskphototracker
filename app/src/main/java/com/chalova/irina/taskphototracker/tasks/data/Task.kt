package com.chalova.irina.taskphototracker.tasks.data

import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import java.time.Instant

data class Task(
    val id: Long = 0,
    val userId: String,
    val title: String,
    val priority: Priority,
    val description: String? = null,
    val dueDate: Instant,
    val createdAt: Instant = Instant.now(),
    val isCompleted: Boolean = false,
    val reportPhoto: String? = null
)
