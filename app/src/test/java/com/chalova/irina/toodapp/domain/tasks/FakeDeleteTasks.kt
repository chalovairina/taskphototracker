package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.domain.DeleteTasks
import com.chalova.irina.taskphototracker.utils.Result

class FakeDeleteTasks(private val tasksProvider: TasksProvider): DeleteTasks {

    override suspend fun invoke(taskIds: List<Long>): Result<Nothing> {
        tasksProvider.deleteTasks(taskIds)

        return Result.Success()
    }
}