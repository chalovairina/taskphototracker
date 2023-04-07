package com.chalova.irina.todoapp.user_profile.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.user_profile.data.repository.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@AppScope
class DefaultUserDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
): UserDataSource {

    override fun getUserData(): Flow<CurrentUserPreferences> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userName = preferences[PreferencesKeys.userName] ?: ""
                val userImageUrl = preferences[PreferencesKeys.userImageUrl] ?: ""
                val userEmail = preferences[PreferencesKeys.userEmail] ?: ""
                CurrentUserPreferences(userName, userImageUrl, userEmail)
            }
    }

    override suspend fun updateUser(user: User) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userName] = user.name ?: ""
            preferences[PreferencesKeys.userImageUrl] = user.image ?: ""
            preferences[PreferencesKeys.userEmail] = user.email ?: ""
        }
    }

    override suspend fun updateUserName(userId: String, userName: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userName] = userName ?: ""
        }
    }

    override suspend fun updateUserEmail(userId: String, userEmail: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userEmail] = userEmail ?: ""
        }
    }

    override suspend fun updateUserImageUrl(userId: String, userImageUrl: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userImageUrl] = userImageUrl ?: ""
        }
    }

    private object PreferencesKeys {
        val userName = stringPreferencesKey(AppConfig.USER_NAME)
        val userImageUrl = stringPreferencesKey(AppConfig.USER_IMAGE)
        val userEmail = stringPreferencesKey(AppConfig.USER_EMAIL)
    }

}

data class CurrentUserPreferences(
    var userName: String,
    var userImageUrl: String,
    val userEmail: String
)