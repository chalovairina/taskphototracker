package com.chalova.irina.todoapp.login_auth.presentation.login

//sealed class LoginStatus(val title: Titles) {
//
//    object NotDefined : LoginStatus(Titles.NotDefined)
//    object LoggedIn : LoginStatus(Titles.LoggedIn)
//    object LoggedOut : LoginStatus(Titles.LoggedOut)
//
//    companion object {
//        fun getLoginStatusByName(title: String): LoginStatus {
//            return when (Titles.valueOf(title)) {
//                Titles.NotDefined -> NotDefined
//                Titles.LoggedIn -> LoggedIn
//                Titles.LoggedOut -> LoggedOut
//            }
//        }
//    }

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
//}



