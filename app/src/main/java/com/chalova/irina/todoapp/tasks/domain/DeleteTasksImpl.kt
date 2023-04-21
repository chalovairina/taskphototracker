package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result

class DeleteTasksImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : DeleteTasks {

    override suspend fun invoke(taskIds: List<Long>): Result<Nothing> {
        return getCurrentUserId()?.let { userId ->
            taskRepository.deleteTasks(userId, taskIds)
        } ?: Result.Error(ErrorResult.UserError())
    }
}