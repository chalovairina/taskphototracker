package com.chalova.irina.todoapp.login.data.repository

import android.net.Uri
import com.chalova.irina.todoapp.config.AuthConfig
import com.chalova.irina.todoapp.di.AppScope
import com.google.gson.JsonObject
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AppScope
class VkAuthClient @Inject constructor(): AuthClient {

    override val authBaseUrl: Uri = Uri.parse(VK_AUTH_URL)
    override val authUrlAuthority = VK_URL_AUTHORITY
    override val scope: String = AuthConfig.SCOPE_EMAIL
    override val redirectUri: Uri = Uri.parse(VK_REDIRECT_URI)

    override fun getToken(responseUrl: String): String? {
        val accessToken = extractPattern(responseUrl, "access_token=(.*?)&")

        return accessToken
    }

    override fun getUserId(responseUrl: String): String? {
        val userId = extractPattern(responseUrl, "user_id=(\\d*)")

        return userId
    }

    override fun isResponseSuccessful(response: JsonObject): Boolean {
        // TODO - заглушка из-за непонятного поведения vk api
        if (response.has(ERROR_RESPONSE_ROOT) &&
            response[ERROR_RESPONSE_ROOT].asJsonObject.has("error_msg") &&
            response[ERROR_RESPONSE_ROOT].asJsonObject["error_msg"].asString == "Anonymous token is invalid") {
            return true
        }

        return response.has(NORMAL_RESPONSE_ROOT)
    }
    override fun isErrorResponse(response: JsonObject): Boolean {
        // TODO - заглушка из-за непонятного поведения vk api
        if (response.has(ERROR_RESPONSE_ROOT) &&
            response[ERROR_RESPONSE_ROOT].asJsonObject.has("error_msg") &&
            response[ERROR_RESPONSE_ROOT].asJsonObject["error_msg"].asString == "Anonymous token is invalid") {
            return false
        }

        return response.has(ERROR_RESPONSE_ROOT)
    }

    override val authUrlParams: Map<String, String> = mapOf(
        AuthConfig.CLIENT_ID to VK_CLIENT_ID,
        AuthConfig.REDIRECT_URI to redirectUri.toString(),
        AuthConfig.DISPLAY to AuthConfig.DISPLAY_MOBILE,
        AuthConfig.SCOPE to scope,
        AuthConfig.RESPONSE_TYPE to AuthConfig.RESPONSE_TYPE_TOKEN,
        AuthConfig.STATE to AuthConfig.AUTH_STATE
    )

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