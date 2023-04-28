package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.AuthData
import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope

class GetCurrentAuthDataImpl(
    private val externalScope: CoroutineScope,
    private val authRepository: AuthRepository
) : GetCurrentAuthData {

    override suspend fun invoke(): AuthData {
        return authRepository.getAuthData()
    }
}