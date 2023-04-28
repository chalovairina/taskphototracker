package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.utils.Result

interface DeleteAllTasks {

    suspend operator fun invoke(): Result<Nothing>
}