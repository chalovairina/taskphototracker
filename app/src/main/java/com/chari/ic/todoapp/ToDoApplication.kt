package com.chari.ic.todoapp

import android.app.Application
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.repository.ToDoRepository

class ToDoApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val database = ToDoDatabase.getDatabase(this)
        val repository = ToDoRepository.initialize(database.getToDoTaskDao())
    }
}