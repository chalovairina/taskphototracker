package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.utils.DispatcherProvider
import com.chalova.irina.todoapp.utils.StandardDispatcherProvider
import dagger.Binds
import dagger.Module

@Module
interface DispatchersModule {

    @Binds
    fun dispatcherProvider(standardDispatcherProvider: StandardDispatcherProvider): DispatcherProvider
}