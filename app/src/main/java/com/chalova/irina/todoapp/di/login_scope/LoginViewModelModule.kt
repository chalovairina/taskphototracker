package com.chalova.irina.todoapp.di.login_scope

import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface LoginViewModelModule {

    @Binds
    @LoginScope
    fun viewModelFactory(factory: LoginViewModelFactory): ViewModelProvider.Factory
}