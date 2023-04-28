package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.domain.GetTask

class FakeGetTask(private val tasksProvider: TasksProvider): GetTask {

    override suspend fun invoke(taskId: Long): Task? {
        return tasksProvider.getTasks().find { it.id == taskId }
    }
}