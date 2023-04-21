package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result

class DeleteAllTasksImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : DeleteAllTasks {

    override suspend fun invoke(): Result<String> {
        return getCurrentUserId()?.let { userId ->
            taskRepository.deleteAllTasks(userId)
        } ?: Result.Error(ErrorResult.UserError())
    }
}