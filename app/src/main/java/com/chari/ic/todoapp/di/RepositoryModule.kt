package com.chari.ic.todoapp.di

import com.chari.ic.todoapp.repository.datastore.DataStoreRepository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.ToDoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindToDoRepository(repository: ToDoRepository): Repository

    @Binds
    abstract fun bindDataStoreRepository(dataStoreRepository: DataStoreRepository): IDataStoreRepository
}
