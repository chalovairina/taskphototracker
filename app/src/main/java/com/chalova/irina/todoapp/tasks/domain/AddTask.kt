package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.utils.Result
import java.time.LocalDate

interface AddTask {

    suspend operator fun invoke(task: Task): Result<Task>
    suspend operator fun invoke(
        taskId: Long, title: String,
        description: String? = null, priority: Priority, dueDate: LocalDate
    ): Result<Task>
}