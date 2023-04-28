package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import com.chalova.irina.taskphototracker.utils.Result
import java.time.LocalDate

interface AddTask {

    suspend operator fun invoke(task: Task): Result<Task>
    suspend operator fun invoke(
        taskId: Long, title: String,
        description: String? = null, priority: Priority, dueDate: LocalDate
    ): Result<Task>
}