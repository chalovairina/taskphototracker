package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.todoapp.login_auth.data.AuthData
import com.chalova.irina.todoapp.login_auth.domain.GetCurrentAuthData

class FakeGetCurrentAuthData: GetCurrentAuthData {

    private var userId: String? = "userId"
    private var token: String? = "123abc"

    override suspend fun invoke(): AuthData {
        return AuthData(userId, token)
    }
}