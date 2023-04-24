package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.todoapp.login_auth.domain.UpdateLoginStatus
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus
import com.chalova.irina.todoapp.utils.Result

class FakeUpdateLoginStatus: UpdateLoginStatus {

    private var status: LoginStatus = LoginStatus.LoggedIn

    override suspend fun invoke(loginStatus: LoginStatus): Result<Nothing> {
        this.status = loginStatus
        return Result.Success()
    }
}