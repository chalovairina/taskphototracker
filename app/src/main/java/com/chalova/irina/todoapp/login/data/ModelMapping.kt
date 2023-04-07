package com.chalova.irina.todoapp.login.data

import com.chalova.irina.todoapp.login.data.local.AuthPreferences
import com.chalova.irina.todoapp.login.data.repository.AuthDetails

fun AuthPreferences.toExternal() = AuthDetails(
    userId = userId.ifEmpty { null },
    token = token.ifEmpty { null },
    loginStatus = loginStatus.ifEmpty { null }
)

fun AuthDetails.toLocal() = AuthPreferences(
    userId = userId ?: "",
    token = token ?: "",
    loginStatus = loginStatus ?: ""
)