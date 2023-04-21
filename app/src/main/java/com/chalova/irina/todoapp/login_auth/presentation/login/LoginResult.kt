package com.chalova.irina.todoapp.login_auth.presentation.login

sealed class LoginResult {

    object NotDefined : LoginResult()
    data class Failed(val message: String? = null) : LoginResult()
    data class Success(val userId: String, val token: String) : LoginResult()
}
