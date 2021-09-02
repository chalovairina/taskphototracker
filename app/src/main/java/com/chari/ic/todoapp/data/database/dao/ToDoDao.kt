package com.chari.ic.todoapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chari.ic.todoapp.data.database.entities.ToDoTask

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo_tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<ToDoTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(toDoTask: ToDoTask)

    @Update
    suspend fun updateTask(toDoTask: ToDoTask)

    @Delete
    suspend fun deleteTask(toDoTask: ToDoTask)

    @Query("DELETE FROM todo_tasks")
    suspend fun deleteAll()

    @Query("SELECT * FROM todo_tasks WHERE title LIKE :searchQuery ORDER BY id DESC")
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoTask>>
}