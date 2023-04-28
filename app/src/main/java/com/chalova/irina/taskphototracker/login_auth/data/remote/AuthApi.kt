package com.chalova.irina.taskphototracker.login_auth.data.remote

import com.chalova.irina.taskphototracker.di.app_scope.AppScope
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

@AppScope
interface AuthApi {

    @GET("/method/users.get")
    suspend fun authenticate(
        @Query("users_ids") userId: String,
        @Query("access_token") token: String,
        @Query("v") apiVersion: String
    ): JsonObject
}