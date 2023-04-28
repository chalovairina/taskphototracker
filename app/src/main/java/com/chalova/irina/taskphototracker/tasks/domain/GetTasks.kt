package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task
import kotlinx.coroutines.flow.Flow

interface GetTasks {

    operator fun invoke(): Flow<List<Task>>
}