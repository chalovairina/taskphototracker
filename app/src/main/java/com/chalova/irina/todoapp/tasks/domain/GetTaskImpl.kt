package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.login_auth.exception.UserNotFoundException
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository

class GetTaskImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : GetTask {

    override suspend fun invoke(taskId: Long): Task? {
        return getCurrentUserId()?.let { userId ->
            taskRepository.getTask(userId, taskId)
        } ?: throw UserNotFoundException()
    }
}