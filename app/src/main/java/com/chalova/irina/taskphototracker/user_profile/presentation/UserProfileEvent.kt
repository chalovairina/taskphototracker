package com.chalova.irina.taskphototracker.user_profile.presentation

import android.net.Uri

sealed class UserProfileEvent {

    data class UserNameChanged(val name: String) : UserProfileEvent()
    data class EmailChanged(val email: String) : UserProfileEvent()
    data class ImageUriChanged(val uri: Uri) : UserProfileEvent()
    object SaveUserData : UserProfileEvent()
}
