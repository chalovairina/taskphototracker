package com.chari.ic.todoapp

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(
    application: Application,
    private val repository: ToDoRepository
): AndroidViewModel(application) {
    val getAllData: LiveData<List<ToDoTask>> = repository.getAllData

    fun insertToDoTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(toDoTask)
        }
    }

    fun updateToDoTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(toDoTask)
        }
    }

    fun deleteToDoTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(toDoTask)
        }
    }
}