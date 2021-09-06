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

@TypeConverters(Converter::class)
@Database(
    entities = [ToDoTask::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun getToDoDao(): ToDoDao
}