package com.chari.ic.todoapp.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.chari.ic.todoapp.utils.Constants
import dagger.hilt.android.scopes.ActivityRetainedScoped
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
}