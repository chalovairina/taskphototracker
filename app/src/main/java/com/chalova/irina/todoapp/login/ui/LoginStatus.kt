package com.chalova.irina.todoapp.login.ui

sealed class LoginStatus(val title: Titles) {

    object NotDefined: LoginStatus(Titles.NotDefined)
    object LoggedIn: LoginStatus(Titles.LoggedIn)
    object LoggedOut: LoginStatus(Titles.LoggedOut)

    companion object {
        fun getLoginStatusByName(title: String): LoginStatus {
            return when (Titles.valueOf(title)) {
                Titles.NotDefined -> NotDefined
                Titles.LoggedIn -> LoggedIn
                Titles.LoggedOut -> LoggedOut
            }
        }
    }

    enum class Titles {
        NotDefined,
        LoggedIn,
        LoggedOut
    }
}



