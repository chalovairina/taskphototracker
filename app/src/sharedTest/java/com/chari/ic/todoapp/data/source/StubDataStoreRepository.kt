package com.chari.ic.todoapp.data.source

import com.chari.ic.todoapp.repository.IDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubDataStoreRepository @Inject constructor(): IDataStoreRepository {
    override fun readUserLoggedIn(): Flow<Boolean> {
        return flow { emit(true) }
    }

    override suspend fun writeUserLoggedIn(userLoggedIn: Boolean) {

    }

}