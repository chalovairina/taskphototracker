package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.taskphototracker.login_auth.domain.GetUserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetUserId: GetUserId {

    private var userId: String? = "userId"

    override fun invoke(): Flow<String?> {
        return flow { userId }
    }
}