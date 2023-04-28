package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.utils.Result

class CompleteTaskImpl(
    private val taskRepository: TaskRepository
) : CompleteTask {

    override suspend fun invoke(task: Task): Result<Nothing> {
        return taskRepository.completeTask(task.userId, task.id)
    }
}