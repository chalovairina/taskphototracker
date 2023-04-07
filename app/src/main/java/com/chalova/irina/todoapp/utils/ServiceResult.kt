package com.chalova.irina.todoapp.utils

sealed class ServiceResult<out T> {

    data class Success<out T>(val value: T): ServiceResult<T>()
    data class Error(val code: Int? = null, val errorResult: ErrorResult): ServiceResult<Nothing>()
}
