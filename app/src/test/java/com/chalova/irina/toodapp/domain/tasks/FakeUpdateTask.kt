package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.domain.UpdateTask
import com.chalova.irina.taskphototracker.utils.Result

class FakeUpdateTask(private val tasksProvider: TasksProvider): UpdateTask {

    override suspend fun invoke(updatedTask: Task): Result<Nothing> {
        tasksProvider.updateTask(updatedTask)
        return Result.Success()
    }

}