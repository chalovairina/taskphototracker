package com.chalova.irina.todoapp.tasks.data.util

sealed class DatabaseResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): DatabaseResult<T>(data)
    class Empty<T>: DatabaseResult<T>()
    class Loading<T>: DatabaseResult<T>()
}