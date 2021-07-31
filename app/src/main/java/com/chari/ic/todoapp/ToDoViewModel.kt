package com.chari.ic.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(
    application: Application,
    private val repository: ToDoRepository
): AndroidViewModel(application) {
    val getAllTasks: LiveData<List<ToDoTask>> = repository.getAllTasks

    fun insertTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(toDoTask)
        }
    }

    fun updateTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(toDoTask)
        }
    }

    fun deleteTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(toDoTask)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}