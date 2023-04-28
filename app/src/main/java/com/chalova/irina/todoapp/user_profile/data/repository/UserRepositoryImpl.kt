package com.chalova.irina.todoapp.user_profile.data.repository

import com.chalova.irina.todoapp.di.app_scope.AppScope
import com.chalova.irina.todoapp.user_profile.data.local.UserDao
import com.chalova.irina.todoapp.user_profile.data.toExternal
import com.chalova.irina.todoapp.user_profile.data.toLocal
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.SQLException
import javax.inject.Inject

@AppScope
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserStream(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { user ->
            user?.toExternal()
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            userDao.insertUser(user.toLocal())
            Result.Success(user)
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }
}