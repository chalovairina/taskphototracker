package com.chalova.irina.todoapp.user_profile.data.repository

import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.user_profile.data.local.UserDao
import com.chalova.irina.todoapp.user_profile.data.toExternal
import com.chalova.irina.todoapp.user_profile.data.toLocal
import com.chalova.irina.todoapp.utils.StandardDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AppScope
class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val dispatcherProvider: StandardDispatcherProvider
) : UserRepository {

    override suspend fun getUserData(userId: String): Flow<User?> {
        return userDao.getUser(userId).map { user ->
            user?.toExternal()
        }
    }

    override suspend fun updateUser(user: User) {
        userDao.insertUser(user.toLocal())
    }

    override suspend fun updateUserName(userId: String, userName: String?) {
        userDao.updateUserName(userId, userName)
    }

    override suspend fun updateUserEmail(userId: String, userEmail: String?) {
        userDao.updateUserEmail(userId, userEmail)
    }

    override suspend fun updateUserImageUrl(userId: String, userImageUrl: String?) {
        userDao.updateUserImageUrl(userId, userImageUrl)
    }

}