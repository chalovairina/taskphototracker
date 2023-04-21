package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result

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