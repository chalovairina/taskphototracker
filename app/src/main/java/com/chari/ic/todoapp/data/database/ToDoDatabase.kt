package com.chari.ic.todoapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.DateConverter
import com.chari.ic.todoapp.data.database.entities.PriorityConverter
import com.chari.ic.todoapp.data.database.entities.ToDoTask

@TypeConverters(PriorityConverter::class, DateConverter::class)
@Database(
    entities = [ToDoTask::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun getToDoDao(): ToDoDao
}