package com.chalova.irina.taskphototracker.tasks.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chalova.irina.taskphototracker.tasks.data.util.DateConverter
import com.chalova.irina.taskphototracker.tasks.data.util.PriorityConverter
import com.chalova.irina.taskphototracker.user_profile.data.local.LocalUser
import com.chalova.irina.taskphototracker.user_profile.data.local.UserDao

const val TASKS_DB_NAME = "todo_db"

@TypeConverters(PriorityConverter::class, DateConverter::class)
@Database(
    version = 2,
    entities = [LocalTask::class, LocalUser::class],
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {


    abstract val tasksDao: TasksDao
    abstract val userDao: UserDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE todo_tasks ADD COLUMN photo_name TEXT DEFAULT NULL")
    }
}
