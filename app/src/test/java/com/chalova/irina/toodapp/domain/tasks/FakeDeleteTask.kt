package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.domain.DeleteTask
import com.chalova.irina.taskphototracker.utils.Result

class FakeDeleteTask(private val tasksProvider: TasksProvider): DeleteTask {

    override suspend fun invoke(taskId: Long): Result<Long> {
        tasksProvider.deleteTask(taskId)

        return Result.Success()
    }
}