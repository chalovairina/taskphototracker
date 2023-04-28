package com.chalova.irina.taskphototracker.di.login_scope

import com.chalova.irina.taskphototracker.login_auth.presentation.login.WebViewLoginActivity
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