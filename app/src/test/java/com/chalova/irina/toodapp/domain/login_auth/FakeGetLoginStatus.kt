package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.taskphototracker.login_auth.domain.GetLoginStatus
import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetLoginStatus: GetLoginStatus {

    private var status: LoginStatus = LoginStatus.LoggedIn

    override fun invoke(): Flow<LoginStatus> {
        return flow { status }
    }
}