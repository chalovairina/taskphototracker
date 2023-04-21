package com.chalova.irina.todoapp.user_profile.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserStream(userId: String): Flow<User?>

    suspend fun updateUser(user: User): com.chalova.irina.todoapp.utils.Result<User>

//    suspend fun updateUserName(userId: String, userName: String?)
//    suspend fun updateUserEmail(userId: String, userEmail: String?)
//    suspend fun updateUserImageUrl(userId: String, userImageUrl: String?)

}