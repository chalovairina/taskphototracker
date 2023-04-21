package com.chalova.irina.todoapp.login_auth.domain

import kotlinx.coroutines.flow.Flow

interface GetUserId {

    operator fun invoke(): Flow<String?>
}