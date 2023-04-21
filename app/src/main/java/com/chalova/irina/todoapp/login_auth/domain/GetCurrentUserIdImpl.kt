package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepository

class GetCurrentUserIdImpl(
    private val authRepository: AuthRepository
) : GetCurrentUserId {

    override suspend fun invoke(): String? {
        return authRepository.getUserId()
    }
}