package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.data.AuthData

interface GetCurrentAuthData {

    suspend operator fun invoke(): AuthData
}