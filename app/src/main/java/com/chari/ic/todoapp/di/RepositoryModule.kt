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
//    @MainRepository
    abstract fun bindToDoRepository(repository: ToDoRepository): Repository

    @Binds
//    @MainDataStoreRepository
    abstract fun bindDataStoreRepository(dataStoreRepository: DataStoreRepository): IDataStoreRepository
}

//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class MainRepository
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class MainDataStoreRepository