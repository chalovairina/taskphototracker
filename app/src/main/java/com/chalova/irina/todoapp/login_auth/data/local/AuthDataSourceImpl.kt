package com.chalova.irina.todoapp.login_auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.config.AuthConfig
import com.chalova.irina.todoapp.di.app_scope.AppScope
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@AppScope
class AuthDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthDataSource {

    override suspend fun updateAuthData(userId: String?, token: String?, loginStatus: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userId] = userId ?: ""
            preferences[PreferencesKeys.token] = token ?: ""
            preferences[PreferencesKeys.loginStatus] = loginStatus
        }
    }

    override fun observeAuthData(): Flow<AuthPreferences> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userId = preferences[PreferencesKeys.userId] ?: ""
                val token = preferences[PreferencesKeys.token] ?: ""
                val loginStatus = preferences[PreferencesKeys.loginStatus] ?: ""
                AuthPreferences(userId, token, loginStatus)
            }
    }

    override suspend fun getAuthData(): AuthPreferences {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userId = preferences[PreferencesKeys.userId] ?: ""
                val token = preferences[PreferencesKeys.token] ?: ""
                val loginStatus = preferences[PreferencesKeys.loginStatus] ?: ""
                AuthPreferences(userId, token, loginStatus)
            }.first()
    }

    override fun observeUserId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.userId]?.ifEmpty { null }
            }
    }

    override suspend fun getUserId(): String? {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.userId]
            }.firstOrNull()
    }

    override suspend fun getToken(): String? {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.token]
            }.firstOrNull()
    }

    override suspend fun writeToken(userId: String?, token: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userId] = userId ?: ""
            preferences[PreferencesKeys.token] = token ?: ""
        }
    }

    override suspend fun writeLoginStatus(loginStatus: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.loginStatus] = loginStatus
        }
    }

    override fun observeLoginStatus(): Flow<String?> {
        Timber.d("observeLoginStatus call")
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.loginStatus]
            }
    }

    private object PreferencesKeys {
        val userId = stringPreferencesKey(AppConfig.USER_ID)
        val token = stringPreferencesKey(AuthConfig.TOKEN)
        val loginStatus = stringPreferencesKey(AuthConfig.LOGIN_STATUS)
    }
}

data class AuthPreferences(
    val userId: String,
    val token: String,
    val loginStatus: String
)