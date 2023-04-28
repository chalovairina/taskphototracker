package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.domain.GetSearchQueryTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetSearchQueryTasks(private val tasksProvider: TasksProvider): GetSearchQueryTasks {

    override suspend fun invoke(query: String): Flow<List<Task>> {
        return flow {
            emit(tasksProvider.getTasks()
                .filter {
                    it.title.contains(query)
                })
        }
    }
}