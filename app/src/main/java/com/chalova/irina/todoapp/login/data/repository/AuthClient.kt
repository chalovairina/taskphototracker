package com.chalova.irina.todoapp.login.data.repository

import android.net.Uri
import com.google.gson.JsonObject

interface AuthClient {

    val authBaseUrl: Uri
    val authUrlAuthority: String
    val authUrlParams: Map<String, String>
    val scope: String
    val redirectUri: Uri

    fun getToken(responseUrl: String): String?
    fun getUserId(responseUrl: String): String?
    fun isResponseSuccessful(response: JsonObject): Boolean
    fun isErrorResponse(response: JsonObject): Boolean
}