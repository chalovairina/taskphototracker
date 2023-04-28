package com.chalova.irina.taskphototracker.user_profile.data.repository

data class User(
    val id: String,
    var name: String? = null,
    val email: String? = null,
    var image: String? = null
)
