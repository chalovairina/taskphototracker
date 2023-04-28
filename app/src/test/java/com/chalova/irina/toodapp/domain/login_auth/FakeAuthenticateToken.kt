package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.taskphototracker.login_auth.domain.AuthenticateToken
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

class FakeAuthenticateToken: AuthenticateToken {

    private var userId: String? = "userId"
    private var token: String? = "123abc"

    override suspend fun invoke(userId: String, token: String): Result<Nothing> {
        return if (this.userId == userId && this.token == token) Result.Success()
        else Result.Error(ErrorResult.OAuthError())
    }
}