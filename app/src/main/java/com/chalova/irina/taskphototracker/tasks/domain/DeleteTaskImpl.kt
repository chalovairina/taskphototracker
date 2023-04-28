package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

class DeleteTaskImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : DeleteTask {

    override suspend fun invoke(taskId: Long): Result<Long> {
        return getCurrentUserId()?.let { userId ->
            taskRepository.deleteTask(userId, taskId)
        } ?: Result.Error(ErrorResult.UserError())
    }
}