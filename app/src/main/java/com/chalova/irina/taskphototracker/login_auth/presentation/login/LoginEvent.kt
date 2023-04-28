package com.chalova.irina.taskphototracker.login_auth.presentation.login

sealed class LoginEvent {
    data class UrlLoadStart(val url: String) : LoginEvent()
    data class UrlLoaded(val url: String) : LoginEvent()
    data class LoadFailed(val value: String? = null) : LoginEvent()
}
