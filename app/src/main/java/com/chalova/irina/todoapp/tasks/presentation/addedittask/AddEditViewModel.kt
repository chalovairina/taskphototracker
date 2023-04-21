package com.chalova.irina.todoapp.tasks.presentation.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.domain.TasksUseCases
import com.chalova.irina.todoapp.tasks.presentation.utils.NavigationArgs.CURRENT_TASK_ID
import com.chalova.irina.todoapp.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddEditViewModel @AssistedInject constructor(
    private val tasksUseCases: TasksUseCases,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface AddEditViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): AddEditViewModel
    }

    private val _addEditTaskState: MutableStateFlow<AddEditTaskState> =
        MutableStateFlow(AddEditTaskState())
    val addEditTaskState: StateFlow<AddEditTaskState> = _addEditTaskState.asStateFlow()

    private val _userMessage = MutableSharedFlow<Int>()
    val userMessage: SharedFlow<Int> = _userMessage

    private val currentTaskId: Long = savedStateHandle[CURRENT_TASK_ID] ?: -1

    init {
        if (currentTaskId != -1L) {
            loadEditedTask()
        }
    }

    private fun loadEditedTask() {
        viewModelScope.launch {
            tasksUseCases.getTask(currentTaskId)?.also { task ->
                _addEditTaskState.update {
                    it.copy(
                        titleState = it.titleState.copy(title = task.title),
                        descriptionState = it.descriptionState.copy(description = task.description),
                        dueDateState = it.dueDateState.copy(dueDate = DateTimeUtil.toLocalDate(task.dueDate)),
                        priorityState = it.priorityState.copy(priority = task.priority)
                    )
                }
            } ?: run {
                throw IllegalArgumentException("Task not found")
            }
        }
    }

    fun onEvent(event: AddEditTaskEvent) {
        when (event) {
            is AddEditTaskEvent.TitleChanged -> {
                _addEditTaskState.update {
                    it.copy(
                        titleState = it.titleState.copy(title = event.title)
                    )
                }
            }
            is AddEditTaskEvent.DescriptionChanged -> {
                _addEditTaskState.update {
                    it.copy(
                        descriptionState = it.descriptionState.copy(description = event.description)
                    )
                }
            }
            is AddEditTaskEvent.DueDateChanged -> {
                _addEditTaskState.update {
                    it.copy(
                        dueDateState = it.dueDateState.copy(dueDate = event.dueDate)
                    )
                }
            }
            is AddEditTaskEvent.PriorityChanged -> {
                _addEditTaskState.update {
                    it.copy(
                        priorityState = it.priorityState.copy(priority = event.priority)
                    )
                }
            }
            is AddEditTaskEvent.SaveAddEditTask -> {
                saveTask()
            }
        }
    }

    private fun saveTask() {
        if (taskHasErrorInput()) {
            return
        }

        viewModelScope.launch {
            val result = tasksUseCases.addTask(
                currentTaskId,
                _addEditTaskState.value.titleState.title,
                _addEditTaskState.value.descriptionState.description,
                _addEditTaskState.value.priorityState.priority,
                _addEditTaskState.value.dueDateState.dueDate
            )
            when (result) {
                is Result.Error -> _userMessage.emit(R.string.addedit_unknown_error)
                is Result.Success -> {
                    _addEditTaskState.update {
                        it.copy(isTaskSaved = true)
                    }
                }
            }
        }
    }

    private fun taskHasErrorInput(): Boolean {
        var hasErrorInput = false
        if (addEditTaskState.value.titleState.title.isBlank()) {
            _addEditTaskState.update {
                it.copy(
                    titleState = it.titleState.copy(error = TaskState.Error.EmptyValue())
                )
            }
            hasErrorInput = true
        } else {
            _addEditTaskState.update {
                it.copy(
                    titleState = it.titleState.copy(error = null)
                )
            }
        }
        if (addEditTaskState.value.dueDateState.dueDate < LocalDate.now()) {
            _addEditTaskState.update {
                it.copy(
                    dueDateState = it.dueDateState.copy(error = TaskState.Error.PastDate())
                )
            }
            hasErrorInput = true
        } else {
            _addEditTaskState.update {
                it.copy(
                    dueDateState = it.dueDateState.copy(error = null)
                )
            }
        }

        return hasErrorInput
    }

}