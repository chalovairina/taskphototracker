package com.chari.ic.todoapp

import com.chari.ic.todoapp.repository.datastore.CurrentUserPreferences
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataStoreRepository @Inject constructor():IDataStoreRepository {
    private var loggedIn = false
    override fun readUserLoggedIn(): Flow<Boolean> {
        return flow { emit(loggedIn) }
    }

    override suspend fun writeUserLoggedIn(userLoggedIn: Boolean) {
        loggedIn = userLoggedIn
    }

    private var currentUser = CurrentUserPreferences("", "", 0L,
        "", "", "")

    override fun readCurrentUserData(): Flow<CurrentUserPreferences> {
        return flow { emit(currentUser) }
    }

    override suspend fun writeCurrentUserData(
        userId: String,
        userName: String,
        userMobile: Long,
        userImageUrl: String,
        userEmail: String,
        fcmToken: String
    ) {
        currentUser = CurrentUserPreferences(userId, userName, userMobile, userImageUrl, userEmail, fcmToken)
    }

    override suspend fun writeCurrentUserName(userName: String) {
        currentUser.userName = userName
    }

    override suspend fun writeCurrentUserMobile(userMobile: Long) {
        currentUser.userMobile = userMobile
    }

    override suspend fun writeCurrentUserImageUrl(userImageUrl: String) {
        currentUser.userImageUrl = userImageUrl
    }

    override suspend fun writeCurrentUserFcmToken(fcmToken: String) {
        currentUser.fcmToken = fcmToken
    }
}