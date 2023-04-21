package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result
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