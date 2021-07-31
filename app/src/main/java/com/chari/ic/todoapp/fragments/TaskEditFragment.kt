package com.chari.ic.todoapp.fragments

import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.utils.Constants

open class TaskEditFragment: Fragment() {

    val listener: AdapterView.OnItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                Constants.PRIORITY_POSITION_HIGH -> (parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(requireContext().applicationContext,
                            Constants.PRIORITY_COLOR_HIGH
                        ))
                Constants.PRIORITY_POSITION_MEDIUM -> (parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(requireContext().applicationContext,
                            Constants.PRIORITY_COLOR_MEDIUM
                        ))
                Constants.PRIORITY_POSITION_LOW -> (parent?.getChildAt(0) as TextView)
                    .setTextColor(
                        ContextCompat.getColor(requireContext().applicationContext,
                            Constants.PRIORITY_COLOR_LOW
                        ))
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

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