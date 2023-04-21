package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task
import kotlinx.coroutines.flow.Flow

interface GetSearchQueryTasks {

    suspend operator fun invoke(query: String): Flow<List<Task>>
}