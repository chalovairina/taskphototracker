package com.chari.ic.todoapp.repository

import androidx.lifecycle.LiveData
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import java.lang.IllegalArgumentException

class ToDoRepository private constructor(
    private val toDoDao: ToDoDao
) {

    val getAllTasks: LiveData<List<ToDoTask>> = toDoDao.getAllTasks()

    suspend fun insertTask(toDoTask: ToDoTask) {
        toDoDao.insertTask(toDoTask)
    }

    suspend fun updateTask(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask)
    }

    suspend fun deleteTask(toDoTask: ToDoTask) {
        toDoDao.deleteTask(toDoTask)
    }

    suspend fun deleteAll() {
        toDoDao.deleteAll()
    }

    companion object {
        private var INSTANCE: ToDoRepository? = null

        fun initialize(toDoDao: ToDoDao) {
            if (INSTANCE == null) {
                INSTANCE = ToDoRepository(toDoDao)
            }
        }

        fun getRepository(): ToDoRepository {
            return INSTANCE ?:
            throw IllegalArgumentException("Repository must be initialized")
        }
    }
}