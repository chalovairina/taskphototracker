package com.chalova.irina.todoapp.binding

import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.tasks.ui.addedittask.TaskState
import com.chalova.irina.todoapp.utils.PriorityUtils
import java.time.LocalDate

object AddEditTaskBinding {

    @BindingAdapter("android:hint")
    @JvmStatic
    fun updateHint(view: TextView, error: TaskState.Error?) {
        error?.let {
            when (view.id) {
                R.id.title_hint -> {
                    view.text = when (error) {
                        is TaskState.Error.EmptyValue -> view.resources.getString(R.string.empty_title)
                        else -> view.resources.getString(R.string.title_hint)
                    }
                }
                R.id.date_hint -> view.text = when (error) {
                    is TaskState.Error.PastDate -> view.resources.getString(R.string.past_date)
                    else -> view.resources.getString(R.string.choose_due_date)
                }
            }
            view.setTextColor(ContextCompat.getColor(view.context, R.color.red))
        } ?: run {
            view.text = when (view.id) {
                R.id.title_hint -> view.resources.getString(R.string.title_hint)
                R.id.date_hint -> view.resources.getString(R.string.choose_due_date)
                else -> ""
            }
        }
    }

    @BindingAdapter("formatDate")
    @JvmStatic
    fun formatDate(view: TextView, date: LocalDate?) {
        date?.let {
            view.text = DateTimeUtil.formatDate(DateTimeUtil.toInstant(date))
        }
    }

    @BindingAdapter("setPriority")
    @JvmStatic
    fun setPriority(
        view: View,
        priority: Priority?
    ) {
            priority?.let {
                when(view) {
                    is Spinner -> {
                        when (priority) {
                            Priority.HIGH -> {
                                view.setSelection(PriorityUtils.PRIORITY_POSITION_HIGH)
                            }
                            Priority.MEDIUM -> view.setSelection(PriorityUtils.PRIORITY_POSITION_MEDIUM)
                            Priority.LOW -> view.setSelection(PriorityUtils.PRIORITY_POSITION_LOW)
                        }
                    }
                }
            }
        }

}