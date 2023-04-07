package com.chalova.irina.todoapp.login.data.local

import kotlinx.coroutines.flow.Flow

interface AuthDataSource {

    suspend fun updateAuthData(userId: String?,
                               token: String?,
                               loginStatus: String)

    fun getAuthData(): Flow<AuthPreferences>
    fun getUserIdStream(): Flow<String?>
    suspend fun getUserId(): String?

    suspend fun writeToken(userId: String?, token: String?)
    suspend fun writeLoginStatus(loginStatus: String)
    fun getLoginStatus(): Flow<String?>
    suspend fun getToken(): String?
}