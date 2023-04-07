package com.chalova.irina.toodapp

import android.net.Uri
import com.chalova.irina.todoapp.login.data.repository.AuthConnectionDetails
import com.chalova.irina.todoapp.login.data.repository.AuthDetails
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.login.ui.LoginStatus
import com.chalova.irina.todoapp.utils.ServiceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthRepository : AuthRepository {

    private var status: LoginStatus = LoginStatus.NotDefined
    private var userId: String? = "userId"
    private var token: String? = "123abc"
    private val testUri = "http://example.com"
    private val authConDetails = AuthConnectionDetails(
        Uri.parse(testUri),
        "example.com", mapOf(),
        Uri.parse(testUri))

    override suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus) {
        this.userId = userId
        this.token = token
        this.status = loginStatus
    }

    override val authConnectionData = authConDetails

    override val authData: Flow<AuthDetails> = flow { AuthDetails(userId, token, status.title.name) }

    override val userIdStream: Flow<String?> = flow { userId }

    override suspend fun getUserId(): String? {
        return userId
    }

    override suspend fun getToken(): String? {
        return token
    }

    override suspend fun writeToken(userId: String?, token: String?) {
        this.userId = userId
        this.token = token
    }

    override suspend fun <T> authenticate(userId: String, token: T): ServiceResult<T> {
        return ServiceResult.Success(token)
    }

    override fun extractToken(responseUrl: String): String {
        return "123abc"
    }

    override fun extractUserId(responseUrl: String): String {
        return "userId"
    }

    override val loginStatus: Flow<LoginStatus?> = flow { status }


    override suspend fun writeLoginStatus(loginStatus: LoginStatus) {
        this.status = loginStatus
    }

    override suspend fun logout() {
        userId = null
        token = null
        status = LoginStatus.LoggedOut
    }

}
