package com.chalova.irina.todoapp.login_auth.domain


interface GetCurrentUserId {

    suspend operator fun invoke(): String?
}