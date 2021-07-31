package com.chari.ic.todoapp.fragments

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.utils.Constants

open class TaskEditFragment: Fragment() {
    protected fun getPriority(priority: String): Priority {
        return when (priority) {
            getString(R.string.priority_high) -> Priority.HIGH
            getString(R.string.priority_medium) -> Priority.MEDIUM
            getString(R.string.priority_low) -> Priority.LOW
            else -> Priority.LOW
        }
    }

    protected fun getPriorityPosition(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> Constants.PRIORITY_POSITION_HIGH
            Priority.MEDIUM -> Constants.PRIORITY_POSITION_MEDIUM
            Priority.LOW -> Constants.PRIORITY_POSITION_LOW
        }
    }

    protected fun verifyDataFromUser(title: String): Boolean {
        return title.isNotEmpty()
    }

    protected fun makeToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}