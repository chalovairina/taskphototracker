package com.chalova.irina.todoapp.di

import android.content.Context
import com.chalova.irina.todoapp.ToDoApplication
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [
    CoroutineScopeModule::class,
    DatabaseModule::class,
    DataStoreModule::class,
    RepositoryModule::class,
    ViewModelModule::class,
    WorkerModule::class,
    ApiModule::class
]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(app: ToDoApplication)

    fun activityComponentFactory(): ActivityComponent.Factory
    fun loginComponentFactory(): LoginComponent.Factory
}