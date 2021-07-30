package com.chari.ic.todoapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chari.ic.todoapp.data.database.entities.ToDoTask

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo_tasks ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<ToDoTask>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(toDoTask: ToDoTask)
}