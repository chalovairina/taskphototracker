package com.chalova.irina.todoapp.login.util

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.chalova.irina.todoapp.config.AppConfig

val Context.dataStore by preferencesDataStore(
    name = AppConfig.APP_DATA_STORE
)