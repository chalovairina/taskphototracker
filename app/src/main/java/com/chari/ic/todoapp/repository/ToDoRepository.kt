package com.chari.ic.todoapp.repository

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException

class ToDoRepository private constructor(
    private val toDoDao: ToDoDao
) : Repository {
    @VisibleForTesting
    val mutex = Mutex()

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

    companion object {
        @Volatile
        private var INSTANCE: ToDoRepository? = null

        fun initialize(toDoDao: ToDoDao) {
            synchronized(this) {
                val instance = ToDoRepository(toDoDao)
                INSTANCE = instance
            }
        }

        fun getRepository(): ToDoRepository {
            return INSTANCE ?:
            throw UnsupportedOperationException("Repository must be initialized first")
        }
    }

    @VisibleForTesting
    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            insertTask(task)
        }
    }


    @VisibleForTesting
    override suspend fun resetRepository() =
        mutex.withLock  {
            // Clear all data to avoid test pollution.
//            deleteAll()
            INSTANCE = null
        }
//        synchronized(this) {
//            runBlocking {
//                deleteAll()
//                INSTANCE = null
//            }
//
//        }
}