package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task

interface GetTask {

    suspend operator fun invoke(taskId: Long): Task?
}