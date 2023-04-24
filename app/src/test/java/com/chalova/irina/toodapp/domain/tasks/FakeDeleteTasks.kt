package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.todoapp.tasks.domain.DeleteTasks
import com.chalova.irina.todoapp.utils.Result

class FakeDeleteTasks(private val tasksProvider: TasksProvider): DeleteTasks {

    override suspend fun invoke(taskIds: List<Long>): Result<Nothing> {
        tasksProvider.deleteTasks(taskIds)

        return Result.Success()
    }
}