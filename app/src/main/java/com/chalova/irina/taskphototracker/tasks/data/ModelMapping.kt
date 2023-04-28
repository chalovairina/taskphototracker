package com.chalova.irina.taskphototracker.tasks.data

import com.chalova.irina.taskphototracker.tasks.data.source.local.LocalTask

// External to local
fun Task.toLocal() = LocalTask(
    id = id,
    userId = userId,
    title = title,
    priority = priority,
    description = description,
    dueDate = dueDate,
    createdAt = createdAt,
    isCompleted = isCompleted,
    reportPhoto = reportPhoto
)

fun List<Task>.toLocal() = map(Task::toLocal)

// Local to External
fun LocalTask.toExternal() = Task(
    id = id,
    userId = userId,
    title = title,
    priority = priority,
    description = description,
    dueDate = dueDate,
    createdAt = createdAt,
    isCompleted = isCompleted,
    reportPhoto = reportPhoto
)

fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)