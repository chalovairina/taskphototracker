package com.chalova.irina.todoapp.login.ui

import com.chalova.irina.todoapp.utils.ErrorResult

data class AuthState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val userMessage: Int? = null,
    val authError: ErrorResult? = null
)
