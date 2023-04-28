package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.utils.Result

interface DeleteTask {

    suspend operator fun invoke(taskId: Long): Result<Long>
}