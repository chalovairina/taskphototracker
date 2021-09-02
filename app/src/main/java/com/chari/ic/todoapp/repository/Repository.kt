package com.chari.ic.todoapp.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.room.OnConflictStrategy
import com.chari.ic.todoapp.data.database.entities.ToDoTask

interface Repository {
    val cachedTasks: LiveData<List<ToDoTask>>

    suspend fun insertTask(toDoTask: ToDoTask)

    suspend fun updateTask(toDoTask: ToDoTask)

    suspend fun deleteTask(toDoTask: ToDoTask)

    suspend fun deleteAll()

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoTask>>

    @VisibleForTesting
    suspend fun fillTasksRepo(vararg tasks: ToDoTask)

    @VisibleForTesting
    suspend fun resetRepository()
}