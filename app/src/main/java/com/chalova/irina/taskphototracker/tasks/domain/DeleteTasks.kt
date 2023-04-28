package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.utils.Result

interface DeleteTasks {

    suspend operator fun invoke(taskIds: List<Long>): Result<Nothing>
}