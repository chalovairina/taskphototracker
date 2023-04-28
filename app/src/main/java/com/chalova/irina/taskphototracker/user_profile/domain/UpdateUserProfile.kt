package com.chalova.irina.taskphototracker.user_profile.domain

import com.chalova.irina.taskphototracker.user_profile.data.repository.User

interface UpdateUserProfile {

    suspend operator fun invoke(
        userName: String?,
        userEmail: String?,
        userImageUri: String?
    ): com.chalova.irina.taskphototracker.utils.Result<User>
}