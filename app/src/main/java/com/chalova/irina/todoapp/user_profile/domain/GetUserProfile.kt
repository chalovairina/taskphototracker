package com.chalova.irina.todoapp.user_profile.domain

import com.chalova.irina.todoapp.user_profile.data.repository.User
import kotlinx.coroutines.flow.Flow

interface GetUserProfile {

    operator fun invoke(): Flow<User?>
}