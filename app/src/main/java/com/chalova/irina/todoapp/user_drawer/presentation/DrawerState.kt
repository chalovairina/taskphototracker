package com.chalova.irina.todoapp.user_drawer.presentation

import android.net.Uri

data class DrawerState(
    val userEmail: String? = null,
    val imageUri: Uri? = null
)