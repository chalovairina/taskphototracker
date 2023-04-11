package com.chalova.irina.todoapp.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.di.LoginScope
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import javax.inject.Inject

@LoginScope
class LoginViewModelFactory @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}