package com.chari.ic.todoapp.repository.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.chari.ic.todoapp.utils.Constants
import com.chari.ic.todoapp.utils.Constants.USER_EMAIL
import com.chari.ic.todoapp.utils.Constants.USER_ID
import com.chari.ic.todoapp.utils.Constants.USER_IMAGE
import com.chari.ic.todoapp.utils.Constants.USER_MOBILE
import com.chari.ic.todoapp.utils.Constants.USER_MOBILE_FCM_TOKEN
import com.chari.ic.todoapp.utils.Constants.USER_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : IDataStoreRepository {
    private object PreferencesKeys {
        val userLoggedIn = booleanPreferencesKey(Constants.DATA_STORE_KEY_USER_LOGGED_IN)
        val userId = stringPreferencesKey(USER_ID)
        val userName = stringPreferencesKey(USER_NAME)
        val userMobile = longPreferencesKey(USER_MOBILE)
        val userImageUrl = stringPreferencesKey(USER_IMAGE)
        val userEmail = stringPreferencesKey(USER_EMAIL)
        val fcmToken = stringPreferencesKey(USER_MOBILE_FCM_TOKEN)
    }

    override suspend fun writeUserLoggedIn(userLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userLoggedIn] = userLoggedIn
        }
    }

    override fun readUserLoggedIn(): Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
                preferences ->
            preferences[PreferencesKeys.userLoggedIn] ?: false
        }

    override suspend fun writeCurrentUserData(userId: String, userName: String, userMobile: Long,
                                             userImageUrl: String, userEmail: String, fcmToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userId] = userId
            preferences[PreferencesKeys.userName] = userName
            preferences[PreferencesKeys.userMobile] = userMobile
            preferences[PreferencesKeys.userImageUrl] = userImageUrl
            preferences[PreferencesKeys.userEmail] = userEmail
            preferences[PreferencesKeys.fcmToken] = fcmToken
        }
    }

    override suspend fun writeCurrentUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userName] = userName
        }
    }

    override suspend fun writeCurrentUserMobile(userMobile: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userMobile] = userMobile
        }
    }

    override suspend fun writeCurrentUserImageUrl(userImageUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.userImageUrl] = userImageUrl
        }
    }

    override suspend fun writeCurrentUserFcmToken(fcmToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.fcmToken] = fcmToken
        }
    }

    override fun readCurrentUserData(): Flow<CurrentUserPreferences> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                    preferences ->
                val userId = preferences[PreferencesKeys.userId] ?: ""
                val userName = preferences[PreferencesKeys.userName] ?: ""
                val userMobile = preferences[PreferencesKeys.userMobile] ?: 0L
                val userImageUrl = preferences[PreferencesKeys.userImageUrl] ?: ""
                val userEmail = preferences[PreferencesKeys.userEmail] ?: ""
                val fcmToken = preferences[PreferencesKeys.fcmToken] ?: ""
                CurrentUserPreferences(userId, userName, userMobile, userImageUrl, userEmail, fcmToken)
            }

        }

}

data class CurrentUserPreferences(
    val userId: String,
    val userName: String,
    val userMobile: Long,
    val userImageUrl: String,
    val userEmail: String,
    val fcmToken: String
)