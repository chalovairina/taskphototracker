package com.chalova.irina.taskphototracker.login_auth.data.repository

import android.net.Uri
import com.chalova.irina.taskphototracker.BuildConfig
import com.chalova.irina.taskphototracker.config.AuthConfig
import com.chalova.irina.taskphototracker.di.app_scope.AppScope
import com.chalova.irina.taskphototracker.login_auth.data.AuthServiceData
import com.google.gson.JsonObject
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AppScope
class VkAuthManager @Inject constructor() : AuthManager {

    override val authApiVer: String = BuildConfig.VK_API_VER
    override val authBaseUrl: Uri = Uri.parse(VK_AUTH_URL)
    override val authUrlAuthority = VK_URL_AUTHORITY
    override val scope: String = AuthConfig.SCOPE_EMAIL
    override val redirectUri: Uri = Uri.parse(VK_REDIRECT_URI)
    override val authUrlParams: Map<String, String> = mapOf(
        AuthConfig.CLIENT_ID to VK_CLIENT_ID,
        AuthConfig.REDIRECT_URI to redirectUri.toString(),
        AuthConfig.DISPLAY to AuthConfig.DISPLAY_MOBILE,
        AuthConfig.SCOPE to scope,
        AuthConfig.RESPONSE_TYPE to AuthConfig.RESPONSE_TYPE_TOKEN,
        AuthConfig.STATE to AuthConfig.AUTH_STATE
    )

    override val authServiceData = AuthServiceData(
        authUrl = authBaseUrl,
        authUrlAuthority = authUrlAuthority,
        authUrlParams = authUrlParams,
        redirectUrl = redirectUri
    )

    override fun getToken(responseUrl: String): String? {
        val accessToken = extractPattern(responseUrl, "access_token=(.*?)&")

        return accessToken
    }

    override fun getUserId(responseUrl: String): String? {
        val userId = extractPattern(responseUrl, "user_id=(\\d*)")

        return userId
    }

    override fun isResponseSuccessful(response: JsonObject): Boolean {
        return response.has(NORMAL_RESPONSE_ROOT)
    }

    override fun isErrorResponse(response: JsonObject): Boolean {
        return response.has(ERROR_RESPONSE_ROOT)
    }

    private fun extractPattern(string: String, pattern: String): String? {
        val p: Pattern = Pattern.compile(pattern)
        val m: Matcher = p.matcher(string)
        return if (!m.find()) null else m.toMatchResult().group(1)
    }

    companion object {
        const val VK_AUTH_URL = "https://oauth.vk.com/authorize"
        const val VK_URL_AUTHORITY = "vk.com"
        const val VK_REDIRECT_URI = "https://oauth.vk.com/blank.html"
        const val VK_CLIENT_ID = "51561491"
        const val ERROR_RESPONSE_ROOT = "error"
        const val NORMAL_RESPONSE_ROOT = "response"
        const val SUCCESS = "success=1"
        const val FAIL = "fail=1"
        const val CANCEL = "cancel=1"
        const val GET_JWT_TOKEN = "access_token="
    }
}