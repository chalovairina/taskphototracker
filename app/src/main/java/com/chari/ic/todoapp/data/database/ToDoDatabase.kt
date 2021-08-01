package com.chari.ic.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.Converter
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.runBlocking

@TypeConverters(Converter::class)
@Database(
    entities = [ToDoTask::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun getToDoDao(): ToDoDao

    companion object {

        private var INSTANCE: ToDoDatabase?  = null

        fun getDatabase(context: Context): ToDoDatabase {
            return INSTANCE ?: createDatabase(context)
//            synchronized(this) {

//            }
        }

        private fun createDatabase(context: Context): ToDoDatabase {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java,
                DATABASE_NAME
            ).build()
            INSTANCE = instance

             return instance
        }
    }
}