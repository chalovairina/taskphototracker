package com.chalova.irina.todoapp.di.app_scope

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.chalova.irina.todoapp.BuildConfig
import com.chalova.irina.todoapp.login_auth.data.local.AuthDataSource
import com.chalova.irina.todoapp.login_auth.data.local.AuthDataSourceImpl
import com.chalova.irina.todoapp.login_auth.data.remote.AuthApi
import com.chalova.irina.todoapp.login_auth.data.repository.AuthManager
import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepository
import com.chalova.irina.todoapp.login_auth.data.repository.AuthRepositoryImpl
import com.chalova.irina.todoapp.login_auth.data.repository.VkAuthManager
import com.chalova.irina.todoapp.login_auth.util.dataStore
import com.chalova.irina.todoapp.photo_reports.data.repository.ReportRepository
import com.chalova.irina.todoapp.photo_reports.data.repository.ReportRepositoryImpl
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepositoryImpl
import com.chalova.irina.todoapp.tasks.data.source.local.MIGRATION_1_2
import com.chalova.irina.todoapp.tasks.data.source.local.TASKS_DB_NAME
import com.chalova.irina.todoapp.tasks.data.source.local.TaskDatabase
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepository
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
object DatabaseModule {

    @Provides
    @AppScope
    fun getDatabase(context: Context) =
        Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            TASKS_DB_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @AppScope
    fun getTaskDao(database: TaskDatabase) = database.tasksDao

    @Provides
    @AppScope
    fun getUserDao(database: TaskDatabase) = database.userDao

}

@Module
interface RepositoryModule {

    @Binds
    @AppScope
    fun bindTaskRepository(repository: TaskRepositoryImpl): TaskRepository

    @Binds
    @AppScope
    fun bindUserRepository(dataStoreRepository: UserRepositoryImpl): UserRepository

    @Binds
    @AppScope
    fun bindAuthDataSource(authDataSource: AuthDataSourceImpl): AuthDataSource

    @Binds
    @AppScope
    fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    @AppScope
    fun bindReportsRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository

    @Binds
    @AppScope
    fun bindAuthClient(authClient: VkAuthManager): AuthManager
}

@Module
object DataStoreModule {

    @Provides
    @AppScope
    fun getDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

@Module
object ApiModule {

    @Provides
    @AppScope
    fun provideVkAuthApi(): AuthApi {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .baseUrl(BuildConfig.VK_API_URL)
            .build()
            .create(AuthApi::class.java)
    }
}
