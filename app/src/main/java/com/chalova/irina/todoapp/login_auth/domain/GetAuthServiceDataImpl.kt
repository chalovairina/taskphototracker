package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.data.AuthServiceData
import com.chalova.irina.todoapp.login_auth.data.repository.AuthManager

class GetAuthServiceDataImpl(
    private val authManager: AuthManager
) : GetAuthServiceData {

    override fun invoke(): AuthServiceData {
        return authManager.authServiceData
    }

    override fun extractToken(input: String): String? {
        return authManager.getToken(input)
    }

    override fun extractUserId(input: String): String? {
        return authManager.getUserId(input)
    }
}