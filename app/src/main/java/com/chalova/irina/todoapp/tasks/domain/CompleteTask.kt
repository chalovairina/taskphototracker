package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task

interface CompleteTask {

    suspend operator fun invoke(task: Task): com.chalova.irina.todoapp.utils.Result<Nothing>
}