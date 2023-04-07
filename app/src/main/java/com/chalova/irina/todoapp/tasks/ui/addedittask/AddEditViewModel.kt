package com.chalova.irina.todoapp.tasks.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.CURRENT_TASK_ID
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.USER_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddEditViewModel @AssistedInject constructor(
    val taskRepository: TaskRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {

    @AssistedFactory
    interface AddEditViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): AddEditViewModel
    }

    private val _titleState = MutableStateFlow(TaskState.TitleState())
    val titleState: StateFlow<TaskState.TitleState> = _titleState.asStateFlow()

    private val _descriptionState = MutableStateFlow(TaskState.DescriptionState())
    val descriptionState: StateFlow<TaskState.DescriptionState> = _descriptionState.asStateFlow()

    private val _dueDateState = MutableStateFlow(TaskState.DueDateState())
    val dueDateState: StateFlow<TaskState.DueDateState> = _dueDateState.asStateFlow()

    private val _priorityState = MutableStateFlow(TaskState.PriorityState())
    val priorityState: StateFlow<TaskState.PriorityState> = _priorityState.asStateFlow()

    private val _isTaskSaved = MutableStateFlow(false)
    val isTaskSaved: StateFlow<Boolean> = _isTaskSaved


    private var currentTaskId: Long = savedStateHandle[CURRENT_TASK_ID] ?: -1

    private var userId: String = savedStateHandle[USER_ID]!!


    init {
        if (currentTaskId != -1L) {
            loadEditedTask()
        }
    }

    private var taskJob: Job? = null

    private fun loadEditedTask() {
        taskJob?.cancel()

        currentTaskId.let { taskId ->
            taskJob = viewModelScope.launch {
                taskRepository.getTask(userId, taskId)?.also { task ->
                    currentTaskId = task.id
                    _titleState.update {
                        it.copy(title = task.title)
                    }
                    _descriptionState.update {
                        it.copy(description = task.description)
                    }
                    _dueDateState.update {
                        it.copy(dueDate = DateTimeUtil.toLocalDate(task.dueDate))
                    }
                    _priorityState.update {
                        it.copy(priority = task.priority)
                    }
                } ?: run {
                    throw IllegalArgumentException("Task not found")
                }
            }
        }
    }

    fun onEvent(event: AddEditTaskEvent) {
        when(event) {
            is AddEditTaskEvent.TitleChanged -> {
                _titleState.update {
                    it.copy(title = event.title)
                }
            }
            is AddEditTaskEvent.DescriptionChanged -> {
                _descriptionState.update {
                    it.copy(description = event.description)
                }
            }
            is AddEditTaskEvent.DueDateChanged -> {
                _dueDateState.update {
                    it.copy(dueDate = event.dueDate)
                }
            }
            is AddEditTaskEvent.PriorityChanged -> {
                _priorityState.update {
                    it.copy(priority = event.priority)
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
            taskRepository.insertTask(
                Task(id = if (currentTaskId == -1L) 0 else currentTaskId,
                    title = _titleState.value.title,
                    description = _descriptionState.value.description,
                    dueDate = DateTimeUtil.toInstant(_dueDateState.value.dueDate),
                    priority = _priorityState.value.priority,
                    userId = userId
                )
            )
            _isTaskSaved.update { true }
        }
    }

    private fun taskHasErrorInput(): Boolean {
        var hasErrorInput = false
        if (titleState.value.title.isBlank()) {
            _titleState.update {
                it.copy(error = TaskState.Error.EmptyValue())
            }
            hasErrorInput = true
        } else {
            _titleState.update {
                it.copy(error = null)
            }
        }
        if (dueDateState.value.dueDate < LocalDate.now()) {
            _dueDateState.update {
                it.copy(error = TaskState.Error.PastDate())
            }
            hasErrorInput = true
        } else {
            _dueDateState.update {
                it.copy(error = null)
            }
        }

        return hasErrorInput
    }

}

fun provideAddEditFactory(
    assistedFactory: AddEditViewModel.AddEditViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }