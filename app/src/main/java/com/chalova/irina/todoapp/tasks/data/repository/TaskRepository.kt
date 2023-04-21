package com.chalova.irina.todoapp.tasks.data.repository

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.utils.Result
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksStream(userId: String): Flow<List<Task>>

    suspend fun getTask(userId: String, taskId: Long): Task?

    suspend fun insertTask(task: Task): Result<Nothing>

    suspend fun insertTasks(tasks: List<Task>): Result<Nothing>

    suspend fun completeTask(userId: String, taskId: Long): Result<Nothing>

    suspend fun deleteTask(userId: String, taskId: Long): Result<Nothing>

    suspend fun deleteTasks(userId: String, taskIds: List<Long>): Result<Nothing>

    suspend fun deleteAllTasks(userId: String): Result<Nothing>

    suspend fun getSearchQueryStream(userId: String, searchQuery: String): Flow<List<Task>>
}