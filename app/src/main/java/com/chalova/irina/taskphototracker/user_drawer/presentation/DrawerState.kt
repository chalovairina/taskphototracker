package com.chalova.irina.taskphototracker.user_drawer.presentation

import android.net.Uri

data class DrawerState(
    val userEmail: String? = null,
    val imageUri: Uri? = null
)