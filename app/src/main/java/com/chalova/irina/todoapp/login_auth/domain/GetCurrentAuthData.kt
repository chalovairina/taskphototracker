package com.chalova.irina.todoapp.login_auth.domain

import com.chalova.irina.todoapp.login_auth.data.AuthData

interface GetCurrentAuthData {

    suspend operator fun invoke(): AuthData
}