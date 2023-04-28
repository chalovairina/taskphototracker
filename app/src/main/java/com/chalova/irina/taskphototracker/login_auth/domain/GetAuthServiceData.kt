package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.AuthServiceData

interface GetAuthServiceData {

    operator fun invoke(): AuthServiceData

    fun extractUserId(input: String): String?
    fun extractToken(input: String): String?
}