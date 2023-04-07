package com.chalova.irina.todoapp.user_profile.data.repository

data class User(
    val id: String,
    var name: String? = null,
    val email: String? = null,
    var image: String? = null
)
