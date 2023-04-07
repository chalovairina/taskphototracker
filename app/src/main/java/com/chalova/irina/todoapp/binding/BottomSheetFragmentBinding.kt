package com.chalova.irina.todoapp.binding

import android.view.View
import android.widget.CalendarView
import androidx.databinding.BindingAdapter
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import java.time.LocalDate

object BottomSheetFragmentBinding {

    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(
        view: View,
        dueDate: LocalDate?
    ) {
        dueDate?.let {
            when(view) {
                is CalendarView -> {
                    view.date = DateTimeUtil.toInstant(dueDate).toEpochMilli()
                }
            }
        }
    }
}