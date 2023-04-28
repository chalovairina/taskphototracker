package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.login_auth.exception.UserNotFoundException
import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository

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