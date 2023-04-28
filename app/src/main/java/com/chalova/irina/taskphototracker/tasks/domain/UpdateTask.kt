package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task

interface UpdateTask {

    suspend operator fun invoke(updatedTask: Task): com.chalova.irina.taskphototracker.utils.Result<Nothing>
}