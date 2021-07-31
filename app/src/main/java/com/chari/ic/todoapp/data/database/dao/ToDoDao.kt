package com.chari.ic.todoapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chari.ic.todoapp.data.database.entities.ToDoTask

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo_tasks ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<ToDoTask>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(toDoTask: ToDoTask)

    @Update
    suspend fun updateTask(toDoTask: ToDoTask)

    @Delete
    suspend fun deleteTask(toDoTask: ToDoTask)

    @Query("DELETE FROM todo_tasks")
    suspend fun deleteAll()
}