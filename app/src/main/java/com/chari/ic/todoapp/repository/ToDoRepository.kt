package com.chari.ic.todoapp.repository

import androidx.lifecycle.LiveData
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import java.lang.IllegalArgumentException

class ToDoRepository private constructor(
    private val toDoDao: ToDoDao
) {

    val getAllData: LiveData<List<ToDoTask>> = toDoDao.getAllTasks()

    suspend fun insert(toDoTask: ToDoTask) {
        toDoDao.insertTask(toDoTask)
    }

    suspend fun update(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask)
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