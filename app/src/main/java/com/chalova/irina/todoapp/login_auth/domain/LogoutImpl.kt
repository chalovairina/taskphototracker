package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepository
import com.chalova.irina.todoapp.utils.Result

class LogoutImpl(
    private val authRepository: AuthRepository
) : Logout {

    override suspend fun invoke(): Result<Nothing> {
        return authRepository.logout()
    }
}