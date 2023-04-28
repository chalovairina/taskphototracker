package com.chalova.irina.taskphototracker.login_auth.domain

import kotlinx.coroutines.flow.Flow

interface GetUserId {

    operator fun invoke(): Flow<String?>
}