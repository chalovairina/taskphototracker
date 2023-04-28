package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.utils.Result

class CompleteTaskImpl(
    private val taskRepository: TaskRepository
) : CompleteTask {

    override suspend fun invoke(task: Task): Result<Nothing> {
        return taskRepository.completeTask(task.userId, task.id)
    }
}