package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.utils.Result

interface DeleteAllTasks {

    suspend operator fun invoke(): Result<Nothing>
}