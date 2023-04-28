package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.login_auth.domain.UserUseCases
import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.tasks.data.util.DateTimeUtil
import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result
import java.time.LocalDate

class AddTaskImpl(
    private val userUseCases: UserUseCases,
    private val taskRepository: TaskRepository
) : AddTask {

    override suspend fun invoke(task: Task): Result<Task> {
        return taskRepository.insertTask(task)
    }

    override suspend fun invoke(
        taskId: Long,
        title: String,
        description: String?,
        priority: Priority,
        dueDate: LocalDate
    ): Result<Task> {
        val userId = userUseCases.getCurrentUserId()
            ?: return Result.Error(ErrorResult.UserError())

        return taskRepository.insertTask(
            Task(
                id = if (taskId == -1L) 0 else taskId,
                title = title,
                description = description,
                dueDate = DateTimeUtil.toInstant(dueDate),
                priority = priority,
                userId = userId
            )
        )
    }
}