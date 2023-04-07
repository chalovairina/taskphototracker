package com.chalova.irina.todoapp.tasks.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chalova.irina.todoapp.tasks.data.util.DateConverter
import com.chalova.irina.todoapp.tasks.data.util.PriorityConverter
import com.chalova.irina.todoapp.user_profile.data.local.LocalUser
import com.chalova.irina.todoapp.user_profile.data.local.UserDao

const val TASKS_DB_NAME = "todo_db"

@TypeConverters(PriorityConverter::class, DateConverter::class)
@Database(
    entities = [LocalTask::class, LocalUser::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase: RoomDatabase() {

    abstract val tasksDao: TasksDao
    abstract val userDao: UserDao
}