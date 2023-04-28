package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task

interface CompleteTask {

    suspend operator fun invoke(task: Task): com.chalova.irina.taskphototracker.utils.Result<Nothing>
}