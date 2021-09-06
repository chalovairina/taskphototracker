package com.chari.ic.todoapp.repository

import kotlinx.coroutines.flow.Flow

interface IDataStoreRepository {
    fun readUserLoggedIn(): Flow<Boolean>

    suspend fun writeUserLoggedIn(userLoggedIn: Boolean)
}