package com.chalova.irina.todoapp.user_profile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val TABLE_USER = "users"

@Entity(tableName = TABLE_USER)
data class LocalUser(
    @ColumnInfo(name = "id") @PrimaryKey val userId: String,
    @ColumnInfo(name = "name") val userName: String?,
    @ColumnInfo(name = "image_url") val userImageUrl: String?,
    @ColumnInfo(name = "email") val userEmail: String?
)
