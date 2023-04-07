package com.chalova.irina.todoapp.login.data.repository

import android.util.Log
import com.chalova.irina.todoapp.BuildConfig
import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.login.data.local.AuthDataSource
import com.chalova.irina.todoapp.login.data.remote.VkAuthApi
import com.chalova.irina.todoapp.login.data.toExternal
import com.chalova.irina.todoapp.login.ui.LoginStatus
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.ServiceResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@AppScope
class VkAuthRepository @Inject constructor(
    private val externalScope: CoroutineScope,
    private val authDataSource: AuthDataSource,
    private val authClient: AuthClient,
    private val authApi: VkAuthApi
): AuthRepository {

    override suspend fun updateAuthData(userId: String, token: String, loginStatus: LoginStatus) {
        authDataSource.updateAuthData(userId, token, loginStatus.title.name)
    }

    override val authConnectionData = AuthConnectionDetails(
        authUrl = authClient.authBaseUrl,
        authUrlAuthority = authClient.authUrlAuthority,
        authUrlParams = authClient.authUrlParams,
        redirectUrl = authClient.redirectUri
    )

    override val authData: Flow<AuthDetails> = authDataSource.getAuthData()
        .map { authPrefs ->
            authPrefs.toExternal()
        }.shareIn(
            scope = externalScope, replay = 1,
            started = SharingStarted.WhileSubscribed(5000L))

    override val userIdStream: Flow<String?> = authDataSource.getUserIdStream().map {
        it?.ifEmpty { null }
    }.shareIn(
        scope = externalScope, replay = 1,
        started = SharingStarted.WhileSubscribed(5000L))
    override suspend fun getUserId(): String? {
        return authDataSource.getUserId()?.ifEmpty { null }
    }

    override suspend fun getToken(): String? {
        return authDataSource.getToken()?.ifEmpty { null }
    }

    override suspend fun writeToken(userId: String?, token: String?) {
        authDataSource.writeToken(userId, token)
    }

    override suspend fun <T> authenticate(userId: String, token: T): ServiceResult<T> {
        return try {
            val response = authApi.authenticate(userId, "Bearer $token", BuildConfig.VK_API_VER)

            if (authClient.isErrorResponse(response)) {
                ServiceResult.Error(errorResult = ErrorResult.OAuthError())
            } else if (authClient.isResponseSuccessful(response)) {
                ServiceResult.Success(token)
            } else {
                ServiceResult.Error(errorResult = ErrorResult.UnknownError())
            }
        } catch(e: HttpException) {
            if (e.code() == 401) {
                ServiceResult.Error(errorResult = ErrorResult.OAuthError())
            } else {
                ServiceResult.Error(errorResult = ErrorResult.NetworkError())
            }
        } catch (e: IOException) {
            ServiceResult.Error(errorResult = ErrorResult.NetworkError())
        } catch (e: Exception) {
            ServiceResult.Error(errorResult = ErrorResult.UnknownError())
        }
    }

    override suspend fun logout() {
        authDataSource.updateAuthData(null, null, LoginStatus.LoggedOut.title.name)
    }

    override fun extractToken(responseUrl: String): String? {
        return authClient.getToken(responseUrl)
    }

    override fun extractUserId(responseUrl: String): String? {
        return authClient.getUserId(responseUrl)
    }

    override val loginStatus: Flow<LoginStatus?> = authDataSource.getLoginStatus().map {
        it?.ifEmpty { null }?.let {
            LoginStatus.getLoginStatusByName(it) }
    }.shareIn(
        scope = externalScope, replay = 1,
        started = SharingStarted.WhileSubscribed(5000L))
    override suspend fun writeLoginStatus(loginStatus: LoginStatus) {
        authDataSource.writeLoginStatus(loginStatus.title.name)
    }
}