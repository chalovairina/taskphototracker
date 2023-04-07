package com.chalova.irina.todoapp.tasks.ui.addedittask

import com.chalova.irina.todoapp.tasks.data.util.Priority
import java.time.LocalDate

sealed class TaskState<T : Any> {

    data class TitleState(val title: String = "", val error: Error? = null):
        TaskState<String>()
    data class DescriptionState(val description: String? = null, val error: Error? = null):
        TaskState<String>()
    data class DueDateState(val dueDate: LocalDate = LocalDate.now(), val error: Error? = null):
        TaskState<LocalDate>()
    data class PriorityState(val priority: Priority = Priority.LOW, val error: Error? = null):
        TaskState<Priority>()

    sealed class Error {
        class EmptyValue(): Error()
        class PastDate(): Error()
    }
}


