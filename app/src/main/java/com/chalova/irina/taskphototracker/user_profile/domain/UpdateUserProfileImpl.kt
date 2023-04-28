package com.chalova.irina.taskphototracker.user_profile.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.user_profile.data.repository.User
import com.chalova.irina.taskphototracker.user_profile.data.repository.UserRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult

class UpdateUserProfileImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val userRepository: UserRepository
) : UpdateUserProfile {

    override suspend fun invoke(
        userName: String?,
        userEmail: String?,
        userImageUri: String?
    ): com.chalova.irina.taskphototracker.utils.Result<User> {
        return getCurrentUserId()?.let { userId ->
            userRepository.updateUser(
                User(
                    userId, userName, userEmail, userImageUri
                )
            )
        } ?: com.chalova.irina.taskphototracker.utils.Result.Error(ErrorResult.UserError())
    }
}