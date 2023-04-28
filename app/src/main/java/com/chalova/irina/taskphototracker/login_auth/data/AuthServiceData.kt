package com.chalova.irina.taskphototracker.login_auth.data

import android.net.Uri

data class AuthServiceData(
    val authUrl: Uri,
    val authUrlAuthority: String,
    val authUrlParams: Map<String, String>,
    val redirectUrl: Uri
)
