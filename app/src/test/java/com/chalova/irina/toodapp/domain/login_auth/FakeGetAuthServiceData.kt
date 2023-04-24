package com.chalova.irina.toodapp.domain.login_auth

import android.net.Uri
import com.chalova.irina.todoapp.login_auth.data.AuthServiceData
import com.chalova.irina.todoapp.login_auth.domain.GetAuthServiceData
import com.chalova.irina.toodapp.testAuthority
import com.chalova.irina.toodapp.testUri

class FakeGetAuthServiceData: GetAuthServiceData {

    private val authServiceData = AuthServiceData(Uri.parse(testUri), testAuthority,
        mapOf(), Uri.parse(testUri))

    override fun invoke(): AuthServiceData {
        return authServiceData
    }

    override fun extractUserId(input: String): String? {
        TODO("Not yet implemented")
    }

    override fun extractToken(input: String): String? {
        TODO("Not yet implemented")
    }
}