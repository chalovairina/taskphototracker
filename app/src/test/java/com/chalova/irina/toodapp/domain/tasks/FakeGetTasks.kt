package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.domain.GetTasks
import kotlinx.coroutines.flow.Flow

class FakeGetTasks(private val tasksProvider: TasksProvider): GetTasks {

    override fun invoke(): Flow<List<Task>> {
        return tasksProvider.getTasksFlow
    }
}