package com.chari.ic.todoapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chari.ic.todoapp.repository.ToDoRepository
import java.lang.IllegalArgumentException

class ToDoViewModelFactory(
//    private val application: Application,
    private val repository: ToDoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Incorrect ViewModel class")

        }
    }
}
