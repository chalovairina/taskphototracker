package com.chalova.irina.todoapp.di

import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.login.ui.LoginViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface LoginViewModelModule {

    @Binds
    @LoginScope
    abstract fun viewModelFactory(factory: LoginViewModelFactory): ViewModelProvider.Factory
}