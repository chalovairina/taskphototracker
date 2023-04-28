package com.chalova.irina.taskphototracker.login_auth.domain

interface Logout {

    suspend operator fun invoke(): com.chalova.irina.taskphototracker.utils.Result<Nothing>
}