package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.domain.GetTasks
import kotlinx.coroutines.flow.Flow

class FakeGetTasks(private val tasksProvider: TasksProvider): GetTasks {

    override fun invoke(): Flow<List<Task>> {
        return tasksProvider.getTasksFlow
    }
}