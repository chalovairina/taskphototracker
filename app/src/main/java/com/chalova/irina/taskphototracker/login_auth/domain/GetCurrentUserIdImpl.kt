package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository

class GetCurrentUserIdImpl(
    private val authRepository: AuthRepository
) : GetCurrentUserId {

    override suspend fun invoke(): String? {
        return authRepository.getUserId()
    }
}