package com.chalova.irina.todoapp.login_auth.data

import com.chalova.irina.todoapp.login_auth.data.local.AuthPreferences

fun AuthPreferences.toExternal() = AuthData(
    userId = userId.ifEmpty { null },
    token = token.ifEmpty { null }
)