package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.login_auth.exception.UserNotFoundException
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
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