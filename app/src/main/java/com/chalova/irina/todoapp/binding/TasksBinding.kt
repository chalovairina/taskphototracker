package com.chalova.irina.todoapp.binding

import android.view.View
import android.view.View.INVISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.DatabaseResult
import com.facebook.shimmer.ShimmerFrameLayout

object TasksBinding {

    @BindingAdapter("setData")
    @JvmStatic
    fun setShimmeringFX(
        view: View,
        databaseStatus: DatabaseResult<List<Task>>?
    ) {
        if (databaseStatus == null) {
            if (view is ShimmerFrameLayout) {
                view.visibility = View.VISIBLE
                view.startShimmer()
            } else {
                view.visibility = INVISIBLE
            }
            return
        }
        when(view) {
            is ShimmerFrameLayout -> {
                when(databaseStatus) {
                    is DatabaseResult.Loading -> {
                        view.visibility = View.VISIBLE
                        view.startShimmer()
                    }
                    else -> {
                        view.stopShimmer();
                        view.visibility = INVISIBLE
                    }
                }
            }
            is RecyclerView ->
                view.isVisible = databaseStatus is DatabaseResult.Success
            is ImageView ->
                view.isVisible = databaseStatus is DatabaseResult.Empty
            is TextView -> {
                view.isVisible = databaseStatus is DatabaseResult.Empty
                databaseStatus.message?.let {
                    view.text = it
                }
            }
        }
    }
}