package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.domain.CompleteTask
import com.chalova.irina.taskphototracker.utils.Result

class FakeCompleteTask(private val tasksProvider: TasksProvider): CompleteTask {

    override suspend fun invoke(task: Task): Result<Nothing> {
        tasksProvider.updateTask(task)
        return Result.Success()
    }
}