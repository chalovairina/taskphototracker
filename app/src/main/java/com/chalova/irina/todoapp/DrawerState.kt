package com.chalova.irina.todoapp

import android.net.Uri

data class DrawerState(
    val userId: String? = null,
    val userEmail: String? = null,
    val imageUri: Uri? = null
)