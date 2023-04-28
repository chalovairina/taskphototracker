package com.chalova.irina.taskphototracker.photo_reports.presentation

import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoResource
import com.chalova.irina.taskphototracker.tasks.utils.TaskOrder

data class ReportsState<T : PhotoResource>(
    val isLoading: Boolean = false,
    val reportsList: List<PhotoReport<T>> = emptyList(),
    val taskOrder: TaskOrder = TaskOrder.Date(TaskOrder.OrderType.Descending)
)