package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.utils.Result

interface DeleteTasks {

    suspend operator fun invoke(taskIds: List<Long>): Result<Nothing>
}