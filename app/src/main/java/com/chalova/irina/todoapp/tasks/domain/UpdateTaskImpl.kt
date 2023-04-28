package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.utils.Result
import timber.log.Timber

class UpdateTaskImpl(
    private val taskRepository: TaskRepository
) : UpdateTask {

    override suspend fun invoke(updatedTask: Task): Result<Nothing> {
        Timber.d("UpdateTask $updatedTask")
        return taskRepository.insertTask(updatedTask)
    }
}