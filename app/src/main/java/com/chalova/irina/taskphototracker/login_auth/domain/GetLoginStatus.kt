package com.chalova.irina.taskphototracker.login_auth.domain

import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import kotlinx.coroutines.flow.Flow

interface GetLoginStatus {

    operator fun invoke(): Flow<LoginStatus>
}