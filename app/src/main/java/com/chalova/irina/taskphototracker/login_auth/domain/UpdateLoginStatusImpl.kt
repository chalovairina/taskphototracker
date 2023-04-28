package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import com.chalova.irina.taskphototracker.utils.Result

class UpdateLoginStatusImpl(
    private val authRepository: AuthRepository
) : UpdateLoginStatus {

    override suspend fun invoke(loginStatus: LoginStatus): Result<Nothing> {
        return authRepository.writeLoginStatus(loginStatus)
    }
}