package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.utils.Result

interface DeleteTask {

    suspend operator fun invoke(taskId: Long): Result<Long>
}