package com.chalova.irina.taskphototracker.login_auth.presentation.auth

import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus

data class AuthState(
    val loginStatus: LoginStatus = LoginStatus.NotDefined,
    val isTokenValid: Boolean = false
)
