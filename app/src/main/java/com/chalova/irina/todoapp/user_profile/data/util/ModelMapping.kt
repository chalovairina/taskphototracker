package com.chalova.irina.todoapp.user_profile.data

import com.chalova.irina.todoapp.user_profile.data.local.LocalUser
import com.chalova.irina.todoapp.user_profile.data.repository.User

fun LocalUser.toExternal(): User {

    return User(
        id = userId,
        name = userName,
        email = userEmail,
        image = userImageUrl
    )
}

fun User.toLocal(): LocalUser {

    return LocalUser(
        userId = id,
        userName = name,
        userEmail = email,
        userImageUrl = image
    )
}