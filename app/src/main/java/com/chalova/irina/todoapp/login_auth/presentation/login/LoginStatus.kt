package com.chalova.irina.todoapp.login_auth.presentation.login

enum class LoginStatus {
    NotDefined,
    LoggedIn,
    LoggedOut;

    companion object {
        @JvmStatic
        fun getLoginStatusByName(status: String): LoginStatus {
            val loginStatus = LoginStatus.values().find { it.name == status }
            return loginStatus ?: throw IllegalArgumentException("no such login status")
        }
    }
}



