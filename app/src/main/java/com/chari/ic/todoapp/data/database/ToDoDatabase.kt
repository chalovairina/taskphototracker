package com.chari.ic.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.Converter
import com.chari.ic.todoapp.data.database.entities.ToDoTask

private const val DATABASE_NAME = "todo_database"
@TypeConverters(Converter::class)
@Database(
    entities = [ToDoTask::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun getToDoTaskDao(): ToDoDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase?  = null

        fun getDatabase(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    DATABASE_NAME
                ) .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}