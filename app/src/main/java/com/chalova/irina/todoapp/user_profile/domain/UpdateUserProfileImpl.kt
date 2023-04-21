package com.chalova.irina.todoapp.user_profile.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.user_profile.data.repository.User
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepository
import com.chalova.irina.todoapp.utils.ErrorResult

class UpdateUserProfileImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val userRepository: UserRepository
) : UpdateUserProfile {

    override suspend fun invoke(
        userName: String?,
        userEmail: String?,
        userImageUri: String?
    ): com.chalova.irina.todoapp.utils.Result<User> {
        return getCurrentUserId()?.let { userId ->
            userRepository.updateUser(
                User(
                    userId, userName, userEmail, userImageUri
                )
            )
        } ?: com.chalova.irina.todoapp.utils.Result.Error(ErrorResult.UserError())
    }
}