package com.chalova.irina.taskphototracker.login_auth.data.repository

import android.net.Uri
import com.chalova.irina.taskphototracker.login_auth.data.AuthServiceData
import com.google.gson.JsonObject

interface AuthManager {

    val authApiVer: String
    val authBaseUrl: Uri
    val authUrlAuthority: String
    val authUrlParams: Map<String, String>
    val scope: String
    val redirectUri: Uri

    val authServiceData: AuthServiceData

    fun getToken(responseUrl: String): String?
    fun getUserId(responseUrl: String): String?
    fun isResponseSuccessful(response: JsonObject): Boolean
    fun isErrorResponse(response: JsonObject): Boolean
}