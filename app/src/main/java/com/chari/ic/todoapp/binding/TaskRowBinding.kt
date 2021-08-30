package com.chari.ic.todoapp.binding

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.utils.PriorityUtils

object TaskRowBinding {
    @BindingAdapter("setPriorityIndicator")
    @JvmStatic
    fun setPriorityIndicator(
        imageView: ImageView,
        priority: Priority
    ) {
        imageView.background.setTint(ContextCompat.getColor(
            imageView.context,
            PriorityUtils.getColorByPriority(priority)
            )
        )
    }
}