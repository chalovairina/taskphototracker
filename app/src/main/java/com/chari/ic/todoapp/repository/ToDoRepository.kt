package com.chari.ic.todoapp.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToDoRepository @Inject constructor(
    private val toDoDao: ToDoDao
) : Repository {

    override val cachedTasks: LiveData<List<ToDoTask>> = toDoDao.getAllTasks()

    override suspend fun insertTask(toDoTask: ToDoTask) {
        toDoDao.insertTask(toDoTask)
    }

    override suspend fun updateTask(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask)
    }

    override suspend fun deleteTask(toDoTask: ToDoTask) {
        toDoDao.deleteTask(toDoTask)
    }

    override suspend fun deleteAll() {
        toDoDao.deleteAll()
    }

    override fun searchDatabase(searchQuery: String) = toDoDao.searchDatabase(searchQuery)

    @VisibleForTesting
    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            insertTask(task)
        }
    }

    @VisibleForTesting
    override suspend fun resetRepository() =
            // Clear all data to avoid test pollution.
            deleteAll()
}