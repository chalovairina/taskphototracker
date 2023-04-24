package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.todoapp.tasks.domain.DeleteTask
import com.chalova.irina.todoapp.utils.Result

class FakeDeleteTask(private val tasksProvider: TasksProvider): DeleteTask {

    override suspend fun invoke(taskId: Long): Result<Long> {
        tasksProvider.deleteTask(taskId)

        return Result.Success()
    }
}