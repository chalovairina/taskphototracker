package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.login.ui.WebViewLoginActivity
import dagger.Subcomponent

@Subcomponent(modules = [LoginViewModelModule::class])
@LoginScope
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: WebViewLoginActivity)
}