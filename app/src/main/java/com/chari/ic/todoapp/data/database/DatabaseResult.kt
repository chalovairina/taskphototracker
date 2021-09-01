package com.chari.ic.todoapp.data.database

sealed class DatabaseResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T): DatabaseResult<T>(data)
    class Empty<T>(data: T? = null, message: String? = null): DatabaseResult<T>(data, message)
    class Loading<T>(data: T? = null): DatabaseResult<T>(data)
}