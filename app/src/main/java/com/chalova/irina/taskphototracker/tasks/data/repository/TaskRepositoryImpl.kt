package com.chalova.irina.taskphototracker.tasks.data.repository

import com.chalova.irina.taskphototracker.di.app_scope.AppScope
import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.source.local.TasksDao
import com.chalova.irina.taskphototracker.tasks.data.toExternal
import com.chalova.irina.taskphototracker.tasks.data.toLocal
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result
import com.chalova.irina.taskphototracker.utils.StandardDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.sql.SQLException
import javax.inject.Inject

@AppScope
class TaskRepositoryImpl @Inject constructor(
    private val dispatchers: StandardDispatcherProvider,
    private val tasksDao: TasksDao
) : TaskRepository {

    override fun getTasksStream(userId: String): Flow<List<Task>> {
        return tasksDao.observeTasks(userId).map { tasks ->
            withContext(dispatchers.default) {
                tasks.toExternal()
            }
        }
    }

    override suspend fun getTask(userId: String, taskId: Long): Task? {
        return tasksDao.getTaskById(userId, taskId)?.toExternal()
    }

    override suspend fun insertTask(task: Task): Result<Nothing> {
        return try {
            tasksDao.insertTask(task.toLocal())
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun insertTasks(tasks: List<Task>): Result<Nothing> {
        return try {
            tasksDao.insertTasks(tasks.map { task -> task.toLocal() })
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun completeTask(userId: String, taskId: Long): Result<Nothing> {
        return try {
            tasksDao.completeTask(userId, taskId)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun deleteTask(userId: String, taskId: Long): Result<Nothing> {
        return try {
            tasksDao.deleteTaskById(userId, taskId)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun deleteTasks(userId: String, taskIds: List<Long>): Result<Nothing> {
        return try {
            tasksDao.deleteAllByUserId(userId, taskIds)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun deleteAllTasks(userId: String): Result<Nothing> {
        return try {
            tasksDao.deleteAllByUserId(userId)
            Result.Success()
        } catch (e: SQLException) {
            Result.Error(ErrorResult.DatabaseError(e))
        }
    }

    override suspend fun getSearchQueryStream(
        userId: String,
        searchQuery: String
    ): Flow<List<Task>> {
        return tasksDao.observeSearchQuery(userId, searchQuery)
            .map { tasks -> tasks.toExternal() }
    }
}