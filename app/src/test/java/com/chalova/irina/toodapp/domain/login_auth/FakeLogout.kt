package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.taskphototracker.login_auth.domain.Logout
import com.chalova.irina.taskphototracker.utils.Result

class FakeLogout: Logout {

    private var userId: String? = "userId"
    private var token: String? = "123abc"

    override suspend fun invoke(): Result<Nothing> {
        userId = null
        token = null
        return Result.Success()
    }
}