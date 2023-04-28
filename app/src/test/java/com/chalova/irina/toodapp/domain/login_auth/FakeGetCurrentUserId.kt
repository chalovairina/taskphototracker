package com.chalova.irina.toodapp.domain.login_auth

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId

class FakeGetCurrentUserId: GetCurrentUserId {

    private var userId: String? = "userId"

    override suspend fun invoke(): String? {
        return userId
    }
}