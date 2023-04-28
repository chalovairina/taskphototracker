package com.chalova.irina.todoapp.tasks.domain

import com.chalova.irina.todoapp.tasks.data.Task

interface UpdateTask {

    suspend operator fun invoke(updatedTask: Task): com.chalova.irina.todoapp.utils.Result<Nothing>
}