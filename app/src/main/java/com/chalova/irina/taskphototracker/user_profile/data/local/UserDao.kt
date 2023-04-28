package com.chalova.irina.taskphototracker.user_profile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id=:userId")
    fun observeUser(userId: String): Flow<LocalUser?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Query("UPDATE users SET name=:userName WHERE id = :userId")
    suspend fun updateUserName(userId: String, userName: String?)

    @Query("UPDATE users SET email=:userEmail WHERE id = :userId")
    suspend fun updateUserEmail(userId: String, userEmail: String?)

    @Query("UPDATE users SET image_url=:userImageUrl WHERE id = :userId")
    suspend fun updateUserImageUrl(userId: String, userImageUrl: String?)
}