package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus

interface UpdateLoginStatus {

    suspend operator fun invoke(loginStatus: LoginStatus): com.chalova.irina.taskphototracker.utils.Result<Nothing>
}