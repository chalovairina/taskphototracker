package com.chalova.irina.todoapp.utils

sealed class Result<out T> {

    data class Success<out T>(val value: T? = null) : Result<T>()
    data class Error(val errorResult: ErrorResult) : Result<Nothing>()
}
