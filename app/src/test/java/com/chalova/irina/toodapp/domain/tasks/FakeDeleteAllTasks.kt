package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.domain.DeleteAllTasks
import com.chalova.irina.taskphototracker.utils.Result

class FakeDeleteAllTasks(private val tasksProvider: TasksProvider): DeleteAllTasks {

    override suspend fun invoke(): Result<Nothing> {
        tasksProvider.deleteTasks()
        return Result.Success()
    }
}