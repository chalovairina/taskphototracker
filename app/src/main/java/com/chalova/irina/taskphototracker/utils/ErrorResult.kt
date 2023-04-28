package com.chalova.irina.taskphototracker.utils

sealed class ErrorResult(val t: Throwable? = null) {

    class NetworkError(throwable: Throwable? = null) : ErrorResult(throwable)
    class OAuthError(throwable: Throwable? = null) : ErrorResult(throwable)
    class DatabaseError(throwable: Throwable? = null) : ErrorResult(throwable)
    class UserError(throwable: Throwable? = null) : ErrorResult(throwable)
    class UnknownError(throwable: Throwable? = null) : ErrorResult(throwable)
}
