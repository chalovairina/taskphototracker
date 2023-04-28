package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetLoginStatusImpl(
    private val externalScope: CoroutineScope,
    private val authRepository: AuthRepository
) : GetLoginStatus {

    override fun invoke(): Flow<LoginStatus> {
        Timber.d("GetLoginStatus")
        return authRepository.loginStatusStream.map { it ?: LoginStatus.LoggedOut }
    }
}