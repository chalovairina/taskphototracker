package com.chalova.irina.todoapp.user_profile.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserStream(userId: String): Flow<User?>

    suspend fun updateUser(user: User): com.chalova.irina.todoapp.utils.Result<User>
}