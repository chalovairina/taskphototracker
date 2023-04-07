package com.chalova.irina.todoapp.user_profile.ui

import android.net.Uri

sealed class UserProfileEvent {

    data class OnUserNameChanged(val name: String): UserProfileEvent()
    data class OnEmailChanged(val email: String): UserProfileEvent()
    data class OnImageUriChanged(val uri: Uri): UserProfileEvent()
    object SaveUserData: UserProfileEvent()
}
