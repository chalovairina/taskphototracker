package com.chalova.irina.todoapp.login.data.repository

import android.net.Uri

data class AuthConnectionDetails(
    val authUrl: Uri,
    val authUrlAuthority: String,
    val authUrlParams: Map<String, String>,
    val redirectUrl: Uri
)
