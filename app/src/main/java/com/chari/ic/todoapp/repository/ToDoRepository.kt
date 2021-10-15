package com.chari.ic.todoapp.repository

import androidx.annotation.VisibleForTesting
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToDoRepository @Inject constructor(
    private val toDoDao: ToDoDao
) : Repository {

    override fun cachedTasks(userId: String): Flow<List<ToDoTask>> {
            return toDoDao.getAllTasksByUserId(userId)
    }

    override suspend fun insertTask(toDoTask: ToDoTask) {
        toDoDao.insertTask(toDoTask)
    }

    override suspend fun updateTask(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask)
    }

    override suspend fun deleteTask(toDoTask: ToDoTask) {
        toDoDao.deleteTask(toDoTask)
    }

    override suspend fun deleteAll(userId: String) {
        toDoDao.deleteAllByUserId(userId)
    }

    override fun searchDatabaseByUserId(searchQuery: String, userId: String) = toDoDao.searchDatabase(
        searchQuery,
        userId
    )

    @VisibleForTesting
    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            insertTask(task)
        }
    }

    @VisibleForTesting
    override suspend fun resetRepository(userId: String) {
        // Clear all data to avoid test pollution.
            deleteAll(userId)
    }
}