package com.chalova.irina.todoapp.login_auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.di.login_scope.LoginScope
import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import javax.inject.Inject

@LoginScope
class LoginViewModelFactory @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}