package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task
import kotlinx.coroutines.flow.Flow

interface GetTasks {

    operator fun invoke(): Flow<List<Task>>
}