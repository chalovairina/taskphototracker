package com.chari.ic.todoapp.binding

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.utils.PriorityUtils

object UpdateBinding {
    @BindingAdapter("setPriority")
    @JvmStatic
    fun setPrioritySpinner(
        spinner: Spinner,
        priority: Priority
    ) {
        when (priority) {
            Priority.HIGH -> spinner.setSelection(PriorityUtils.PRIORITY_POSITION_HIGH)
            Priority.MEDIUM -> spinner.setSelection(PriorityUtils.PRIORITY_POSITION_MEDIUM)
            Priority.LOW -> spinner.setSelection(PriorityUtils.PRIORITY_POSITION_LOW)
        }
    }

}