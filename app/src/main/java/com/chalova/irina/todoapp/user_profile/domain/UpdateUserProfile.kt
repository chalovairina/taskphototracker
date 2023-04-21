package com.chalova.irina.todoapp.user_profile.domain

import com.chalova.irina.todoapp.user_profile.data.repository.User

interface UpdateUserProfile {

    suspend operator fun invoke(
        userName: String?,
        userEmail: String?,
        userImageUri: String?
    ): com.chalova.irina.todoapp.utils.Result<User>
}