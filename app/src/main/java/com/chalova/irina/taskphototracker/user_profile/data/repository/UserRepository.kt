package com.chalova.irina.taskphototracker.user_profile.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserStream(userId: String): Flow<User?>

    suspend fun updateUser(user: User): com.chalova.irina.taskphototracker.utils.Result<User>
}