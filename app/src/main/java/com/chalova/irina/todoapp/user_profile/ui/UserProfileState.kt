package com.chalova.irina.todoapp.user_profile.ui

import android.net.Uri

data class UserProfileState(
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val imageUri: Uri? = null,
    val isSaved: Boolean = false
)
