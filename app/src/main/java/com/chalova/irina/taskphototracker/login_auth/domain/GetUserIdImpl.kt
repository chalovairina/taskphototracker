package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class GetUserIdImpl(
    private val externalScope: CoroutineScope,
    private val authRepository: AuthRepository
) : GetUserId {

    override fun invoke(): Flow<String?> {
        return authRepository.userIdStream
            .shareIn(
                scope = externalScope, replay = 1,
                started = SharingStarted.WhileSubscribed(5000L)
            )
    }
}