package com.chalova.irina.todoapp.tasks.presentation.addedittask

data class AddEditTaskState(
    val titleState: TaskState.TitleState = TaskState.TitleState(),
    val descriptionState: TaskState.DescriptionState = TaskState.DescriptionState(),
    val dueDateState: TaskState.DueDateState = TaskState.DueDateState(),
    val priorityState: TaskState.PriorityState = TaskState.PriorityState(),
    val isTaskSaved: Boolean = false
)
