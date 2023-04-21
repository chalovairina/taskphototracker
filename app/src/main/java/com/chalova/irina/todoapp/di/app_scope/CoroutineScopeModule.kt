package com.chalova.irina.todoapp.di.app_scope

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
object CoroutineScopeModule {

    @Provides
    @AppScope
    fun getExternalCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}