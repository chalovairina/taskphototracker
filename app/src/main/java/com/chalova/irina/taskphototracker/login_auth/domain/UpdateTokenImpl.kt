package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.utils.Result

class UpdateTokenImpl(
    private val authRepository: AuthRepository
) : UpdateToken {

    override suspend fun invoke(userId: String?, token: String?): Result<Nothing> {
        return authRepository.writeToken(userId, token)
    }
}