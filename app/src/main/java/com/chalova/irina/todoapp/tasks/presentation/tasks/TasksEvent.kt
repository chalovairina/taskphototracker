package com.chalova.irina.todoapp.tasks.presentation.tasks

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.utils.TaskOrder

sealed class TasksEvent {

    data class OnOrderChanged(val newOrder: TaskOrder) : TasksEvent()
    data class DeleteTask(val task: Task) : TasksEvent()
    data class DeleteTasks(val taskIds: List<Long>) : TasksEvent()
    object DeleteAll : TasksEvent()
    object RestoreTask : TasksEvent()
    data class OnSearchQueryChanged(val query: String?) : TasksEvent()
}
