package com.chalova.irina.todoapp.di.login_scope

import com.chalova.irina.todoapp.login_auth.presentation.login.WebViewLoginActivity
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