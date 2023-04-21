package com.chalova.irina.todoapp.login_auth.presentation.auth

sealed class AuthEvent {

    object LastLoginUnknown : AuthEvent()
    data class LoginFailed(val value: String? = null) : AuthEvent()
    object Logout : AuthEvent()
}
