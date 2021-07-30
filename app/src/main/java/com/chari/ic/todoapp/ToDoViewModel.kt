package com.chari.ic.todoapp

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
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
    val getAllData: LiveData<List<ToDoTask>> = repository.getAllData

    fun insertToDoTask(toDoTask: ToDoTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(toDoTask)
        }
    }

    // TODO - if no use -> move to fragment
    val listener: AdapterView.OnItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red))
                1 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.yellow))
                2 -> (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.green))
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }
}