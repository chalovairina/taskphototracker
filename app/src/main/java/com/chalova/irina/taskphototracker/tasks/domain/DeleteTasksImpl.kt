package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

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