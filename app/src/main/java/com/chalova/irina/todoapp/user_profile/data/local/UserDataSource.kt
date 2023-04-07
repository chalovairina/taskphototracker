package com.chalova.irina.todoapp.user_profile.data.local

import com.chalova.irina.todoapp.user_profile.data.repository.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun getUserData(): Flow<CurrentUserPreferences>

    suspend fun updateUser(user: User)
    suspend fun updateUserName(userId: String, userName: String?)
    suspend fun updateUserEmail(userId: String, userEmail: String?)
    suspend fun updateUserImageUrl(userId: String, userImageUrl: String?)
}