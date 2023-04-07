package com.chalova.irina.todoapp.binding

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.utils.PriorityUtils
import com.chalova.irina.todoapp.utils.formatDate
import com.google.android.material.chip.Chip
import java.time.Instant

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

    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(
        dateChip: Chip,
        date: Instant
    ) {
        val formattedDate = formatDate(date)
        dateChip.text = formattedDate
    }

    @BindingAdapter("setDescriptionVisibility")
    @JvmStatic
    fun setDescriptionVisibility(
        descriptionTextView: TextView,
        description: String?
    ) {
        if (description == null || description.isBlank()) {
            descriptionTextView.visibility = GONE
        } else {
            descriptionTextView.visibility = VISIBLE
        }
    }
}