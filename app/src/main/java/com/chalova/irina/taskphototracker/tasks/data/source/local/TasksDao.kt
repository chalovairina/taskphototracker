package com.chalova.irina.taskphototracker.tasks.data.source.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

    @Query("SELECT * FROM todo_tasks WHERE user_id = :userId ORDER BY due_date DESC")
    fun observeTasks(userId: String): Flow<List<LocalTask>>

    @Query("SELECT * FROM todo_tasks WHERE user_id = :userId and id = :taskId")
    suspend fun getTaskById(userId: String, taskId: Long): LocalTask?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(toDoTask: LocalTask)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<LocalTask>)

    @Transaction
    @Query("DELETE FROM todo_tasks WHERE user_id = :userId and id = :taskId")
    suspend fun deleteTaskById(userId: String, taskId: Long)

    @Transaction
    @Query("DELETE FROM todo_tasks WHERE user_id = :userId")
    suspend fun deleteAllByUserId(userId: String)

    @Transaction
    @Query("DELETE FROM todo_tasks WHERE user_id = :userId and id in (:taskIds)")
    suspend fun deleteAllByUserId(userId: String, taskIds: List<Long>)

    @Query("SELECT * FROM todo_tasks WHERE user_id = :userId AND title LIKE :searchQuery ORDER BY due_date DESC")
    fun observeSearchQuery(userId: String, searchQuery: String): Flow<List<LocalTask>>

    @Transaction
    @Query("UPDATE todo_tasks SET completed = :isCompleted WHERE user_id = :userId and id = :taskId")
    suspend fun completeTask(userId: String, taskId: Long, isCompleted: Boolean = true)
}