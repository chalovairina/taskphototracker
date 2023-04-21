package com.chalova.irina.todoapp.login_auth.domain

class UserUseCases(
    val getCurrentUserId: GetCurrentUserId,
    val getCurrentAuthData: GetCurrentAuthData,
    val getLoginStatus: GetLoginStatus,
    val getUserId: GetUserId,
    val logout: Logout,
    val authenticateToken: AuthenticateToken,
    val updateToken: UpdateToken,
    val updateLoginStatus: UpdateLoginStatus,
    val getAuthServiceData: GetAuthServiceData
)