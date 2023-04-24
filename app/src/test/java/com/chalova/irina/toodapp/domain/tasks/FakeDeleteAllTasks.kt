package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.todoapp.tasks.domain.DeleteAllTasks
import com.chalova.irina.todoapp.utils.Result

class FakeDeleteAllTasks(private val tasksProvider: TasksProvider): DeleteAllTasks {

    override suspend fun invoke(): Result<String> {
        tasksProvider.deleteTasks()
        return Result.Success()
    }
}