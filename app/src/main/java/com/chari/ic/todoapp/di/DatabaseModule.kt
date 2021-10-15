package com.chari.ic.todoapp.di

import android.content.Context
import androidx.room.Room
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.repository.datastore.DataStoreRepository
import com.chari.ic.todoapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ToDoDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun getToDoDao(database: ToDoDatabase) = database.getToDoDao()
}