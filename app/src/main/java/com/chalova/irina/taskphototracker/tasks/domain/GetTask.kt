package com.chalova.irina.taskphototracker.tasks.domain

import com.chalova.irina.taskphototracker.tasks.data.Task

interface GetTask {

    suspend operator fun invoke(taskId: Long): Task?
}