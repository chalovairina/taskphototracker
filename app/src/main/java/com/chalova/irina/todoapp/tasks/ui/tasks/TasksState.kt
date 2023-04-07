package com.chalova.irina.todoapp.tasks.ui.tasks

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.DatabaseResult
import com.chalova.irina.todoapp.tasks.utils.TaskOrder

data class TasksState(
    val tasksResult: DatabaseResult<List<Task>> = DatabaseResult.Empty(),
    val taskOrder: TaskOrder = TaskOrder.Date(TaskOrder.OrderType.Descending),
    val searchQuery: String? = null,
    val userMessage: Int? = null
)