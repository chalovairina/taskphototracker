package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetUserId
import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class GetTasksImpl(
    private val externalScope: CoroutineScope,
    private val getUserId: GetUserId,
    private val taskRepository: TaskRepository
) : GetTasks {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<List<Task>> {
        return getUserId().flatMapLatest { userId ->
            if (userId != null) {
                taskRepository.getTasksStream(userId)
            } else {
                flow { emptyList<Task>() }
            }
        }
            .stateIn(
                externalScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
    }
}