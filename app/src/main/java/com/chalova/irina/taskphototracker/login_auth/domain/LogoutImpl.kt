package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.utils.Result

class LogoutImpl(
    private val authRepository: AuthRepository
) : Logout {

    override suspend fun invoke(): Result<Nothing> {
        return authRepository.logout()
    }
}