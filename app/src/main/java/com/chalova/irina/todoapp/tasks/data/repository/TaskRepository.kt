package com.chalova.irina.todoapp.tasks.data.repository

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.Priority
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskRepository {

    suspend fun getTasks(userId: String): Flow<List<Task>>

    suspend fun getTask(userId: String, taskId: Long): Task?

    suspend fun insertTask(task: Task)

    suspend fun insertTasks(tasks: List<Task>)

    suspend fun updateTask(userId: String, taskId: Long, title: String,
                           priority: Priority, description: String,
                           dueDate: Instant
    )

    suspend fun completeTask(userId: String, taskId: Long)

    suspend fun deleteTask(userId: String, taskId: Long)

    suspend fun deleteTasks(userId: String, taskIds: List<Long>)

    suspend fun deleteAllTasks(userId: String)

    suspend fun searchQuery(userId: String, searchQuery: String): Flow<List<Task>>
}