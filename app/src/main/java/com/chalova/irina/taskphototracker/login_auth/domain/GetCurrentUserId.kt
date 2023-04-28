package com.chalova.irina.taskphototracker.login_auth.domain


interface GetCurrentUserId {

    suspend operator fun invoke(): String?
}