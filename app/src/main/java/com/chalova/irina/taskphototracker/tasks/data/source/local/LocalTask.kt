package com.chalova.irina.taskphototracker.tasks.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import java.time.Instant

private const val TABLE_TASK = "todo_tasks"

@Entity(tableName = TABLE_TASK)
data class LocalTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    val title: String,
    val priority: Priority,
    val description: String? = null,
    @ColumnInfo(name = "due_date") val dueDate: Instant,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "photo_name") val reportPhoto: String? = null
)