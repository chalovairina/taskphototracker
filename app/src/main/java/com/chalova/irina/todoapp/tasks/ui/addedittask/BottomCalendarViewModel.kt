package com.chalova.irina.todoapp.tasks.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.TASK_DUE_DATE
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class BottomCalendarViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {

    @AssistedFactory
    interface BottomCalendarViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): BottomCalendarViewModel
    }
    
    private val _dueDateState = MutableStateFlow(TaskState.DueDateState())
    val dueDateState: StateFlow<TaskState.DueDateState> = _dueDateState.asStateFlow()

    init {
        savedStateHandle.get<String>(TASK_DUE_DATE)?.let { taskDueDate ->
            viewModelScope.launch {
                _dueDateState.update {
                    TaskState.DueDateState(
                        DateTimeUtil.toLocalDate(taskDueDate))
                }
            }
        }
    }

    fun dateUpdated(year: Int, month: Int, dayOfMonth: Int) {
        _dueDateState.update {
            val dueDate = DateTimeUtil.getLocalDate(year, month , dayOfMonth)
            it.copy(dueDate = dueDate)
        }
    }

    fun dateUpdated(date: Instant) {
        _dueDateState.update {
            it.copy(dueDate = DateTimeUtil.toLocalDate(date))
        }
    }
}

fun provideBottomCalendarFactory(
    assistedFactory: BottomCalendarViewModel.BottomCalendarViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }