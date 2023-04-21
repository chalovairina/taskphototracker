package com.chalova.irina.toodapp

import com.chalova.irina.todoapp.login_auth.data.AuthData
import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepository
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus
import com.chalova.irina.todoapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthRepository : AuthRepository {

    private var status: LoginStatus = LoginStatus.LoggedIn
    private var userId: String? = "userId"
    private var token: String? = "123abc"
    private val testUri = "http://example.com"

    override suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus): Result<String> {
        this.userId = userId
        this.token = token
        this.status = loginStatus
        return Result.Success()
    }

    override val authDataStream: Flow<AuthData> =
        flow { AuthData(userId, token) }

    override val userIdStream: Flow<String?> = flow { userId }

    override suspend fun getUserId(): String? {
        return userId
    }

    override suspend fun getToken(): String? {
        return token
    }

    override suspend fun writeToken(userId: String?, token: String?): Result<Nothing> {
        this.userId = userId
        this.token = token
        return Result.Success()
    }

//    override suspend fun <T> authenticate(userId: String, token: T): Result<T> {
//        return Result.Success(token)
//    }
//
//    override fun extractToken(responseUrl: String): String {
//        return "123abc"
//    }

//    override fun extractUserId(responseUrl: String): String {
//        return "userId"
//    }

    override val loginStatusStream: Flow<LoginStatus?> = flow { status }
    override suspend fun getAuthData(): AuthData {
        return AuthData(userId, token)
    }


    override suspend fun writeLoginStatus(loginStatus: LoginStatus): Result<Nothing> {
        this.status = loginStatus
        return Result.Success()
    }

    override suspend fun authenticate(userId: String, token: String): Result<Nothing> {
        return Result.Success()
    }

    override suspend fun logout(): Result<Nothing> {
        userId = null
        token = null
        status = LoginStatus.LoggedOut
        return Result.Success()
    }

}
