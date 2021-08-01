package com.chari.ic.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(
    private val repository: Repository,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {
    val getAllTasks: LiveData<List<ToDoTask>> = repository.cachedTasks

    fun insertTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.insertTask(toDoTask)
        }
    }

    fun updateTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.updateTask(toDoTask)
        }
    }

    fun deleteTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.deleteTask(toDoTask)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(dispatchers) {
            repository.deleteAll()
        }
    }
}