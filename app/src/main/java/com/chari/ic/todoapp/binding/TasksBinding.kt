package com.chari.ic.todoapp.binding

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter

object TasksBinding {
    @BindingAdapter("setVisibility", "setData", requireAll = false)
    @JvmStatic
    fun setViewVisibilityBasedOnDataAvailability(
        view: View,
        cachedTasks: List<ToDoTask>?,
        adapter: ToDoTaskAdapter?
    ) {
        when(view) {
            is RecyclerView -> {
                val isEmptyData = cachedTasks.isNullOrEmpty()
                view.isInvisible = isEmptyData
                if (!isEmptyData) {
                    adapter?.submitList(cachedTasks)
                }
            }
            else -> {
                view.isVisible = cachedTasks.isNullOrEmpty()
            }
        }
    }
}