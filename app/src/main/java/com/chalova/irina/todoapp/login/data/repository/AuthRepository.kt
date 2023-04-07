package com.chalova.irina.todoapp.login.data.repository

import com.chalova.irina.todoapp.login.ui.LoginStatus
import com.chalova.irina.todoapp.utils.ServiceResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus)

    val authConnectionData: AuthConnectionDetails

    val authData: Flow<AuthDetails>
    val userIdStream: Flow<String?>
    suspend fun getUserId(): String?
    suspend fun getToken(): String?

    suspend fun writeToken(userId: String?, token: String?)

    suspend fun<T> authenticate(userId: String, token: T): ServiceResult<T>

    fun extractToken(responseUrl: String): String?
    fun extractUserId(responseUrl: String): String?

    val loginStatus: Flow<LoginStatus?>
    suspend fun writeLoginStatus(loginStatus: LoginStatus)
    suspend fun logout()

}