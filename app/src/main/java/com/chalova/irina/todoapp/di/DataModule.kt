package com.chalova.irina.todoapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.chalova.irina.todoapp.BuildConfig
import com.chalova.irina.todoapp.login.data.local.AuthDataSource
import com.chalova.irina.todoapp.login.data.local.DefaultAuthDataSource
import com.chalova.irina.todoapp.login.data.remote.VkAuthApi
import com.chalova.irina.todoapp.login.data.repository.AuthClient
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.login.data.repository.VkAuthClient
import com.chalova.irina.todoapp.login.data.repository.VkAuthRepository
import com.chalova.irina.todoapp.login.util.dataStore
import com.chalova.irina.todoapp.tasks.data.repository.DefaultTaskRepository
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.source.local.TASKS_DB_NAME
import com.chalova.irina.todoapp.tasks.data.source.local.TaskDatabase
import com.chalova.irina.todoapp.user_profile.data.repository.DefaultUserRepository
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepository
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
        ).build()

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
    fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository

    @Binds
    @AppScope
    fun bindUserRepository(dataStoreRepository: DefaultUserRepository): UserRepository

    @Binds
    @AppScope
    fun bindAuthDataSource(authDataSource: DefaultAuthDataSource): AuthDataSource

    @Binds
    @AppScope
    fun bindAuthRepository(authRepository: VkAuthRepository): AuthRepository

    @Binds
    @AppScope
    fun bindAuthClient(authClient: VkAuthClient): AuthClient
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
    fun provideVkAuthApi(): VkAuthApi {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create())
            .baseUrl(BuildConfig.VK_API_URL)
            .build()
            .create(VkAuthApi::class.java)
    }
}
