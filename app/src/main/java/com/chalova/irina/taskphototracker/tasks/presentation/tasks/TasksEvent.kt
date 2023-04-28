package com.chalova.irina.taskphototracker.tasks.presentation.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.utils.TaskOrder

sealed class TasksEvent {

    data class OrderChanged(val newOrder: TaskOrder) : TasksEvent()
    data class DeleteTask(val task: Task) : TasksEvent()
    data class DeleteTasks(val taskIds: List<Long>) : TasksEvent()
    object DeleteAll : TasksEvent()
    object RestoreTask : TasksEvent()
    data class SearchQueryChanged(val query: String?) : TasksEvent()

    data class UpdateTask(val updatedTask: Task) : TasksEvent()
    data class CompletingTask(val completingTask: Task) : TasksEvent()
    data class CompletePhotoReport(val photoName: String?) : TasksEvent()
}
