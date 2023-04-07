package com.chalova.irina.todoapp.utils

sealed class ErrorResult(val message: Int? = null) {

    class NetworkError(message: Int? = null): ErrorResult(message)
    class OAuthError(message: Int? = null): ErrorResult(message)
    class DatabaseError(message: Int? = null): ErrorResult(message)
    class UnknownError(message: Int? = null): ErrorResult(message)
}
