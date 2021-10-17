package com.chari.ic.todoapp.binding

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.utils.PriorityUtils
import com.chari.ic.todoapp.utils.formatDate
import com.google.android.material.chip.Chip

object UpdateBinding {

    @BindingAdapter("setTaskToUpdate")
    @JvmStatic
    fun setTaskToUpdate(
        view: View,
        taskToUpdate: ToDoTask?
    ) {
        if (taskToUpdate != null) {
            when(view) {
                is Spinner -> {
                    when (taskToUpdate.priority) {
                        Priority.HIGH -> view.setSelection(PriorityUtils.PRIORITY_POSITION_HIGH)
                        Priority.MEDIUM -> view.setSelection(PriorityUtils.PRIORITY_POSITION_MEDIUM)
                        Priority.LOW -> view.setSelection(PriorityUtils.PRIORITY_POSITION_LOW)
                    }
                }
                is Chip -> {
                    view.text = formatDate(taskToUpdate.dueDate)
                }
                is EditText -> {
                    when(view.id) {
                        R.id.current_title_editText -> view.setText(taskToUpdate.title)
                        R.id.current_description_editText -> view.setText(taskToUpdate.description)
                    }
                }
            }
        }
    }

}