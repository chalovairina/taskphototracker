package com.chalova.irina.todoapp.login_auth.domain

interface AuthenticateToken {

    suspend operator fun invoke(
        userId: String,
        token: String
    ): com.chalova.irina.todoapp.utils.Result<Nothing>
}