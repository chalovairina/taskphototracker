package com.chari.ic.todoapp.data.source

import com.chari.ic.todoapp.repository.datastore.CurrentUserPreferences
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignedOutStubDataStoreRepository @Inject constructor(): IDataStoreRepository {

    override fun readCurrentUserData(): Flow<CurrentUserPreferences> {
        return flow {
            emit(
                CurrentUserPreferences("", "", 0L, "", "", "")
            )}
    }

    override suspend fun writeCurrentUserData(
        userId: String,
        userName: String,
        userMobile: Long,
        userImageUrl: String,
        userEmail: String,
        fcmToken: String
    ) {

    }

    override suspend fun writeCurrentUserName(userName: String) {

    }

    override suspend fun writeCurrentUserMobile(userMobile: Long) {

    }

    override suspend fun writeCurrentUserImageUrl(userImageUrl: String) {

    }

    override suspend fun writeCurrentUserFcmToken(fcmToken: String) {

    }
}