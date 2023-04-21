package com.chalova.irina.todoapp.login_auth.domain

interface Logout {

    suspend operator fun invoke(): com.chalova.irina.todoapp.utils.Result<Nothing>
}