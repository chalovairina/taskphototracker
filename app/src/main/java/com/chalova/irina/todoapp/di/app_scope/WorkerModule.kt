package com.chalova.irina.todoapp.di.app_scope

import androidx.work.Configuration
import com.chalova.irina.todoapp.reminder_work.TodoWorkerFactory
import dagger.Module
import dagger.Provides

@Module
object WorkerModule {

    @Provides
    fun provideWorkManagerConfiguration(
        todoWorkerFactory: TodoWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(todoWorkerFactory)
            .build()
    }
}