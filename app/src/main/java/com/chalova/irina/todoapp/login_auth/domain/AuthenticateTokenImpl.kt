package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepository
import com.chalova.irina.todoapp.utils.Result

class AuthenticateTokenImpl(
    private val authRepository: AuthRepository
) : AuthenticateToken {

    override suspend fun invoke(userId: String, token: String): Result<Nothing> {
        return authRepository.authenticate(userId, token)
    }
}