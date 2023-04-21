package com.chalova.irina.todoapp.login_auth.data.local

import kotlinx.coroutines.flow.Flow

interface AuthDataSource {

    suspend fun updateAuthData(
        userId: String?,
        token: String?,
        loginStatus: String
    )

    fun observeAuthData(): Flow<AuthPreferences>
    suspend fun getAuthData(): AuthPreferences
    fun observeUserId(): Flow<String?>
    suspend fun getUserId(): String?

    suspend fun writeToken(userId: String?, token: String?)
    suspend fun writeLoginStatus(loginStatus: String)
    fun observeLoginStatus(): Flow<String?>
    suspend fun getToken(): String?
}