package com.chalova.irina.todoapp.tasks.data.repository

import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.source.local.TasksDao
import com.chalova.irina.todoapp.tasks.data.toExternal
import com.chalova.irina.todoapp.tasks.data.toLocal
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.utils.StandardDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@AppScope
class DefaultTaskRepository @Inject constructor(
    private val tasksDao: TasksDao,
    private val dispatcherProvider: StandardDispatcherProvider
) : TaskRepository {

    override suspend fun getTasks(userId: String): Flow<List<Task>> {
        return tasksDao.getTasksByUserId(userId).map { tasks ->
            withContext(dispatcherProvider.default) {
                tasks.toExternal()
            }
        }
    }

    override suspend fun getTask(userId: String, taskId: Long): Task? {
        return tasksDao.getTaskById(userId, taskId)?.toExternal()
    }

    override suspend fun insertTask(task: Task) {
        tasksDao.insertTask(task.toLocal())
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        tasksDao.insertTasks(tasks.map { task -> task.toLocal() } )
    }

    override suspend fun updateTask(userId: String, taskId: Long, title: String,
                                    priority: Priority, description: String,
                                    dueDate: Instant) {
        val updatedTask = getTask(userId, taskId)?.copy(
            title = title,
            priority = priority,
            description = description,
            dueDate =  dueDate
        ) ?: throw Exception("Task (id $taskId) not found")

        tasksDao.insertTask(updatedTask.toLocal())
    }

    override suspend fun completeTask(userId: String, taskId: Long) {
        tasksDao.completeTask(userId, taskId)
    }

    override suspend fun deleteTask(userId: String, taskId: Long) {
        tasksDao.deleteTaskById(userId, taskId)
    }

    override suspend fun deleteTasks(userId: String, taskIds: List<Long>) {
        tasksDao.deleteAllByUserId(userId, taskIds)
    }

    override suspend fun deleteAllTasks(userId: String) {
        tasksDao.deleteAllByUserId(userId)
    }

    override suspend fun searchQuery(userId: String, searchQuery: String) =
        tasksDao.searchQuery(userId, searchQuery).map { tasks ->
            withContext(dispatcherProvider.default) {
                tasks.toExternal()
            }
        }
}