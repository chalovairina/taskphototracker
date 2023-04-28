package com.chalova.irina.todoapp.tasks.presentation.tasks

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.utils.TaskOrder

data class TasksState(
    val isLoading: Boolean = false,
    val tasksList: List<Task> = emptyList(),
    val taskOrder: TaskOrder = TaskOrder.Date(TaskOrder.OrderType.Descending)
)