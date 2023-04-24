package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.todoapp.login_auth.domain.UpdateToken
import com.chalova.irina.todoapp.utils.Result

class FakeUpdateToken: UpdateToken {

    private var userId: String? = "userId"
    private var token: String? = "123abc"

    override suspend fun invoke(userId: String?, token: String?): Result<Nothing> {
        this.userId = userId
        this.token = token
        return Result.Success()
    }
}