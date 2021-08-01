package com.chari.ic.todoapp

import android.app.Application
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.repository.ToDoRepository

class ToDoApplication: Application() {
//    val repository: ToDoRepository
//        get() = ServiceLocator.getToDoRepository(this)

    override fun onCreate() {
        super.onCreate()

        val database = ToDoDatabase.getDatabase(this)
        ToDoRepository.initialize(database.getToDoDao())
    }
}