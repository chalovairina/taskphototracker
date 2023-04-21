package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus

interface UpdateLoginStatus {

    suspend operator fun invoke(loginStatus: LoginStatus): com.chalova.irina.todoapp.utils.Result<Nothing>
}