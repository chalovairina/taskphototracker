package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task
import kotlinx.coroutines.flow.Flow

interface GetSearchQueryTasks {

    suspend operator fun invoke(query: String): Flow<List<Task>>
}