package com.chalova.irina.todoapp.login_auth.presentation.auth

import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus

data class AuthState(
    val loginStatus: LoginStatus = LoginStatus.NotDefined,
    val isTokenValid: Boolean = false
)
