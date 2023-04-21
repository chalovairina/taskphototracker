package com.chalova.irina.todoapp.user_profile.presentation

import android.net.Uri

data class UserProfileState(
    val userName: String? = null,
    val userEmail: String? = null,
    val imageUri: Uri? = null,
    val isSaved: Boolean = false
)
