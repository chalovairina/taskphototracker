package com.chari.ic.todoapp.repository

import androidx.annotation.VisibleForTesting
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun cachedTasks(userId: String): Flow<List<ToDoTask>>

    suspend fun insertTask(toDoTask: ToDoTask)

    suspend fun updateTask(toDoTask: ToDoTask)

    suspend fun deleteTask(toDoTask: ToDoTask)

    suspend fun deleteAll(userId: String)

    fun searchDatabaseByUserId(searchQuery: String, userId: String): Flow<List<ToDoTask>>

    @VisibleForTesting
    suspend fun fillTasksRepo(vararg tasks: ToDoTask)

    @VisibleForTesting
    suspend fun resetRepository(userId: String)
}