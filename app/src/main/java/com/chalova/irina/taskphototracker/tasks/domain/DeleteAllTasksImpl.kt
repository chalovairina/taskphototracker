package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

class DeleteAllTasksImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : DeleteAllTasks {

    override suspend fun invoke(): Result<Nothing> {
        return getCurrentUserId()?.let { userId ->
            taskRepository.deleteAllTasks(userId)
        } ?: Result.Error(ErrorResult.UserError())
    }
}