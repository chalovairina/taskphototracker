package com.chalova.irina.taskphototracker.login_auth.util

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.chalova.irina.taskphototracker.config.AppConfig

val Context.dataStore by preferencesDataStore(
    name = AppConfig.APP_DATA_STORE
)