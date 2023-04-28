package com.chalova.irina.taskphototracker.di.app_scope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.taskphototracker.di.activity_scope.ActivityComponent
import com.chalova.irina.taskphototracker.di.login_scope.LoginComponent
import com.chalova.irina.taskphototracker.login_auth.presentation.auth.AuthViewModel
import com.chalova.irina.taskphototracker.user_drawer.presentation.DrawerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(
    includes = [DispatchersModule::class],
    subcomponents = [
        ActivityComponent::class,
        LoginComponent::class
    ]
)
abstract class ViewModelModule {

    @Binds
    @AppScope
    abstract fun viewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @AppScope
    @ViewModelFactory.ViewModelKey(DrawerViewModel::class)
    abstract fun bindDrawerViewModel(
        viewModelFactory: DrawerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @AppScope
    @ViewModelFactory.ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}