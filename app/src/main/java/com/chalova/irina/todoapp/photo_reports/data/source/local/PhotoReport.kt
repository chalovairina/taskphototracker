package com.chalova.irina.todoapp.photo_reports.data.source.local

import java.util.*

open class PhotoReport<T : PhotoResource>(
    val id: String = UUID.randomUUID().toString(),
    val taskId: Long,
    val userId: String,
    val taskTitle: String,
    val photo: T?,
    val isCompleted: Boolean = false
)