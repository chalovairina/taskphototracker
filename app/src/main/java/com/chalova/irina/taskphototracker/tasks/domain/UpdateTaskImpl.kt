package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.utils.Result
import timber.log.Timber

class UpdateTaskImpl(
    private val taskRepository: TaskRepository
) : UpdateTask {

    override suspend fun invoke(updatedTask: Task): Result<Nothing> {
        Timber.d("UpdateTask $updatedTask")
        return taskRepository.insertTask(updatedTask)
    }
}