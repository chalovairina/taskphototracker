package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.login_auth.exception.UserNotFoundException
import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetSearchQueryTasksImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val taskRepository: TaskRepository
) : GetSearchQueryTasks {

    override suspend fun invoke(query: String): Flow<List<Task>> {
        return getCurrentUserId()?.let { userId ->
            taskRepository.getSearchQueryStream(userId, "%$query%")
        } ?: throw UserNotFoundException()
    }
}