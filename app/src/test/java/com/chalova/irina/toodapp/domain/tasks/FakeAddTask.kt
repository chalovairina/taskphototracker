package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.tasks.domain.AddTask
import com.chalova.irina.todoapp.utils.Result
import java.time.LocalDate

class FakeAddTask(private val tasksProvider: TasksProvider): AddTask {

    private val testUserId = "userId"

    override suspend fun invoke(task: Task): Result<Task> {
        tasksProvider.addTask(task)

        return Result.Success()
    }

    override suspend fun invoke(
        taskId: Long,
        title: String,
        description: String?,
        priority: Priority,
        dueDate: LocalDate
    ): Result<Task> {
        val task = Task(
            taskId, testUserId, title, priority, description, DateTimeUtil.toInstant(dueDate)
        )
        tasksProvider.addTask(task)

        return Result.Success()
    }
}