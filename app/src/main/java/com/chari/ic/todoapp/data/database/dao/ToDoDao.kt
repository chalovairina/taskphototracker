package com.chari.ic.todoapp.data.database.dao

import androidx.room.*
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todo_tasks WHERE userId = :userId ORDER BY id DESC")
    fun getAllTasksByUserId(userId: String): Flow<List<ToDoTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(toDoTask: ToDoTask)

    @Update
    suspend fun updateTask(toDoTask: ToDoTask)

    @Delete
    suspend fun deleteTask(toDoTask: ToDoTask)

    @Query("DELETE FROM todo_tasks WHERE userId = :userId")
    suspend fun deleteAllByUserId(userId: String)

    @Query("SELECT * FROM todo_tasks WHERE userId = :userId AND title LIKE :searchQuery ORDER BY id DESC")
    fun searchDatabase(searchQuery: String, userId: String): Flow<List<ToDoTask>>
}