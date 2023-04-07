package com.chalova.irina.todoapp.user_profile.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {


    suspend fun getUserData(userId: String): Flow<User?>

    suspend fun updateUser(user: User)

    suspend fun updateUserName(userId: String, userName: String?)
    suspend fun updateUserEmail(userId: String, userEmail: String?)
    suspend fun updateUserImageUrl(userId: String, userImageUrl: String?)

}