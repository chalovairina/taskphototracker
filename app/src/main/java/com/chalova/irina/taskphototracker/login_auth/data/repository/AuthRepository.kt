package com.chalova.irina.taskphototracker.login_auth.data.repository

import com.chalova.irina.taskphototracker.login_auth.data.AuthData
import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import com.chalova.irina.taskphototracker.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus):
            Result<String>

    val authDataStream: Flow<AuthData>
    val userIdStream: Flow<String?>
    val loginStatusStream: Flow<LoginStatus?>
    suspend fun getAuthData(): AuthData
    suspend fun getUserId(): String?
    suspend fun getToken(): String?

    suspend fun writeToken(userId: String?, token: String?): Result<Nothing>

    suspend fun writeLoginStatus(loginStatus: LoginStatus): Result<Nothing>

    suspend fun authenticate(userId: String, token: String): Result<Nothing>

    suspend fun logout(): Result<Nothing>

}