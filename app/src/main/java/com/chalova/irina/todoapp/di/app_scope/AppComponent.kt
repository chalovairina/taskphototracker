package com.chalova.irina.todoapp.di.app_scope

import android.content.Context
import com.chalova.irina.todoapp.ToDoApplication
import com.chalova.irina.todoapp.di.activity_scope.ActivityComponent
import com.chalova.irina.todoapp.di.login_scope.LoginComponent
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(
    modules = [
        CoroutineScopeModule::class,
        DatabaseModule::class,
        DataStoreModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        WorkerModule::class,
        ApiModule::class,
        UseCaseModule::class
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