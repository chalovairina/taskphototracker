package com.chalova.irina.taskphototracker.tasks.presentation.addedittask

import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import java.time.LocalDate

sealed class AddEditTaskEvent {

    data class TitleChanged(val title: String) : AddEditTaskEvent()
    data class DescriptionChanged(val description: String) : AddEditTaskEvent()
    data class DueDateChanged(val dueDate: LocalDate) : AddEditTaskEvent()
    data class PriorityChanged(val priority: Priority) : AddEditTaskEvent()
    object SaveAddEditTask : AddEditTaskEvent()
}
