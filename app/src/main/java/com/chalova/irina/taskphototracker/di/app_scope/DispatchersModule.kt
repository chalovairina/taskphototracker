package com.chalova.irina.taskphototracker.di.app_scope

import com.chalova.irina.taskphototracker.utils.DispatcherProvider
import com.chalova.irina.taskphototracker.utils.StandardDispatcherProvider
import dagger.Binds
import dagger.Module

@Module
interface DispatchersModule {

    @Binds
    fun dispatcherProvider(standardDispatcherProvider: StandardDispatcherProvider): DispatcherProvider
}