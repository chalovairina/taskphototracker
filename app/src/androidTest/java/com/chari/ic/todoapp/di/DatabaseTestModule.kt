package com.chari.ic.todoapp.di

import android.content.Context
import androidx.room.Room
import com.chari.ic.todoapp.data.database.ToDoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object DatabaseTestModule {
    @Singleton
    @Provides
    @Named("test_db")
    fun getTestInMemoryDatabase(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun getTestToDoDao(@Named("test_db") database: ToDoDatabase) = database.getToDoDao()
}