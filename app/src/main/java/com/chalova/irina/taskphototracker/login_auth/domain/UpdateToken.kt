package com.chalova.irina.taskphototracker.login_auth.domain

interface UpdateToken {

    suspend operator fun invoke(userId: String?, token: String?):
            com.chalova.irina.taskphototracker.utils.Result<Nothing>
}