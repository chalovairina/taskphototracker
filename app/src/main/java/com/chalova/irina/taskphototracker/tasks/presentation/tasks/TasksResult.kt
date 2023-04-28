package com.chalova.irina.taskphototracker.tasks.presentation.tasks

sealed class TasksResult<out T> {

    data class Error(val errorMessage: Int) : TasksResult<Nothing>()
    data class Success<out T>(val data: T) : TasksResult<T>()
}
