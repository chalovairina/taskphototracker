package com.chalova.irina.todoapp.tasks.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chalova.irina.todoapp.tasks.data.util.Priority
import java.time.Instant

private const val TABLE_TASK = "todo_tasks"

@Entity(tableName = TABLE_TASK)
data class LocalTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: String,
    var title: String,
    var priority: Priority,
    var description: String? = null,
    @ColumnInfo(name = "due_date") var dueDate: Instant,
    @ColumnInfo(name = "created_at") var createdAt: Instant,
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false
)