package com.chalova.irina.todoapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.DrawerViewModel
import com.chalova.irina.todoapp.ViewModelFactory
import com.chalova.irina.todoapp.login.ui.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [DispatchersModule::class],
    subcomponents =
    [
        ActivityComponent::class,
        LoginComponent::class])
abstract class ViewModelModule {

    @Binds
    @AppScope
    abstract fun viewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @AppScope
    @ViewModelFactory.ViewModelKey(DrawerViewModel::class)
    abstract fun bindDrawerViewModel(viewModelFactory: DrawerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @AppScope
    @ViewModelFactory.ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}