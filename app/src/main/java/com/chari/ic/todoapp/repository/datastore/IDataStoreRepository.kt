package com.chari.ic.todoapp.repository.datastore

import kotlinx.coroutines.flow.Flow

interface IDataStoreRepository {

    fun readCurrentUserData(): Flow<CurrentUserPreferences>

    suspend fun writeCurrentUserData(userId: String, userName: String, userMobile: Long,
                                     userImageUrl: String, userEmail: String, fcmToken: String)

    suspend fun writeCurrentUserName(userName: String)
    suspend fun writeCurrentUserMobile(userMobile: Long)
    suspend fun writeCurrentUserImageUrl(userImageUrl: String)
    suspend fun writeCurrentUserFcmToken(fcmToken: String)

}