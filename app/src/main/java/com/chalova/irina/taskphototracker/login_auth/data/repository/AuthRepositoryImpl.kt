package com.chalova.irina.taskphototracker.login_auth.data.repository

import com.chalova.irina.taskphototracker.di.app_scope.AppScope
import com.chalova.irina.taskphototracker.login_auth.data.AuthData
import com.chalova.irina.taskphototracker.login_auth.data.local.AuthDataSource
import com.chalova.irina.taskphototracker.login_auth.data.remote.AuthApi
import com.chalova.irina.taskphototracker.login_auth.data.toExternal
import com.chalova.irina.taskphototracker.login_auth.presentation.login.LoginStatus
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import java.sql.SQLException
import javax.inject.Inject

@AppScope
class AuthRepositoryImpl @Inject constructor(
    externalScope: CoroutineScope,
    private val authDataSource: AuthDataSource,
    private val authManager: AuthManager,
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus):
            Result<String> {
        return try {
            authDataSource.updateAuthData(userId, token, loginStatus.name)
            Result.Success(userId)
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override val authDataStream: Flow<AuthData> = authDataSource.observeAuthData()
        .map { authPrefs ->
            authPrefs.toExternal()
        }.shareIn(
            scope = externalScope, replay = 1,
            started = SharingStarted.WhileSubscribed(5000L)
        )

    override val userIdStream: Flow<String?> = authDataSource.observeUserId().map {
        it?.ifEmpty { null }
    }.shareIn(
        scope = externalScope, replay = 1,
        started = SharingStarted.WhileSubscribed(5000L)
    )

    override suspend fun getUserId(): String? {
        return authDataSource.getUserId()?.ifEmpty { null }
    }

    override suspend fun getToken(): String? {
        return authDataSource.getToken()?.ifEmpty { null }
    }

    override suspend fun writeToken(userId: String?, token: String?): Result<Nothing> {
        return try {
            authDataSource.writeToken(userId, token)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun authenticate(userId: String, token: String): Result<Nothing> {
        return try {
            val response = authApi.authenticate(userId, token, authManager.authApiVer)
            if (authManager.isErrorResponse(response)) {
                Result.Error(errorResult = ErrorResult.OAuthError())
            } else if (authManager.isResponseSuccessful(response)) {
                Result.Success()
            } else {
                Result.Error(errorResult = ErrorResult.UnknownError())
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                Result.Error(errorResult = ErrorResult.OAuthError(e))
            } else {
                Result.Error(errorResult = ErrorResult.NetworkError(e))
            }
        } catch (e: IOException) {
            Result.Error(errorResult = ErrorResult.NetworkError(e))
        } catch (e: Exception) {
            Result.Error(errorResult = ErrorResult.UnknownError(e))
        }
    }

    override suspend fun logout(): Result<Nothing> {
        return try {
            authDataSource.updateAuthData(null, null, LoginStatus.LoggedOut.name)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override val loginStatusStream: Flow<LoginStatus?> = authDataSource.observeLoginStatus()
        .map { status ->
            status?.ifEmpty { null }?.let {
                LoginStatus.getLoginStatusByName(it)
            }
        }
        .distinctUntilChanged()
        .shareIn(
            scope = externalScope, replay = 1,
            started = SharingStarted.WhileSubscribed(5000L)
        )

    override suspend fun getAuthData(): AuthData {
        return authDataSource.getAuthData().toExternal()
    }

    override suspend fun writeLoginStatus(loginStatus: LoginStatus): Result<Nothing> {
        return try {
            authDataSource.writeLoginStatus(loginStatus.name)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }
}