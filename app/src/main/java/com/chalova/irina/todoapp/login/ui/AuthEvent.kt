package com.chalova.irina.todoapp.login.ui

sealed class AuthEvent {

    object LastLoginUnknown: AuthEvent()
    data class Login(val userId: String, val token: String) : AuthEvent()
    data class LoginFailed(val value: String? = null) : AuthEvent()
    object Logout: AuthEvent()
}
