package com.chalova.irina.todoapp.tasks.presentation.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.domain.TasksUseCases
import com.chalova.irina.todoapp.tasks.utils.TaskOrder
import com.chalova.irina.todoapp.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class TasksViewModel @AssistedInject constructor(
    private val tasksUseCases: TasksUseCases,
    userUseCases: UserUseCases,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface TasksViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): TasksViewModel
    }

    private val taskOrder: Flow<TaskOrder> = combine(
        savedStateHandle.getStateFlow(AppConfig.TASK_ORDER, TaskOrder.Orders.Date.name),
        savedStateHandle.getStateFlow(AppConfig.ORDER_TYPE, TaskOrder.OrderType.Descending.name)
    ) { taskOrder, orderType ->
        TaskOrder.getTaskOrderByName(taskOrder, orderType)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TaskOrder.Date(TaskOrder.OrderType.Descending)
    )

    private val _isLoading = MutableStateFlow(false)
    private val _tasksResultState = MutableStateFlow<List<Task>>(emptyList())
    private val _userMessage = MutableSharedFlow<Int>()
    val userMessage: SharedFlow<Int> = _userMessage
    val loginStatus = userUseCases.getLoginStatus()

    val tasksState: StateFlow<TasksState> = combine(
        _isLoading, _tasksResultState, taskOrder
    ) { isLoading, tasks, taskOrder ->
        TasksState(
            isLoading = isLoading,
            tasksList = orderTasks(tasks, taskOrder),
            taskOrder = taskOrder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TasksState(isLoading = true)
    )

    private var loadTasksJob: Job? = null

    private var recentlyDeletedTask: Task? = null

    private var lastSearchQuery: String? = null
    private var searchQueryJob: Job? = null

    private var completingTask: Task? = null

    init {
        viewModelScope.launch {
            loadTasks()
        }
    }

    private suspend fun loadTasks() {
        _isLoading.update { true }
        loadTasksJob?.cancel()
        loadTasksJob = viewModelScope.launch {
            tasksUseCases.getTasks().collect { tasks ->
                _tasksResultState.update { tasks }
                _isLoading.update { false }
            }
        }
    }

    private fun orderTasks(tasks: List<Task>, taskOrder: TaskOrder): List<Task> {
        return when (taskOrder.orderType) {
            TaskOrder.OrderType.Ascending -> {
                when (taskOrder) {
                    is TaskOrder.Priority -> tasks.sortedBy { it.priority }.toList()
                    is TaskOrder.Date -> tasks.sortedBy { it.dueDate }.toList()
                }
            }
            TaskOrder.OrderType.Descending -> {
                when (taskOrder) {
                    is TaskOrder.Priority -> tasks.sortedByDescending { it.priority }.toList()
                    is TaskOrder.Date -> tasks.sortedByDescending { it.dueDate }.toList()
                }
            }
        }
    }

    fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.OrderChanged -> {
                if (event.newOrder::class == tasksState.value.taskOrder::class &&
                    event.newOrder.orderType == tasksState.value.taskOrder.orderType
                ) {
                    return
                }
                Timber.d("${TaskOrder.Orders.getOrderByTaskOrder(event.newOrder).name} ${event.newOrder.orderType.name}")
                savedStateHandle[AppConfig.TASK_ORDER] =
                    TaskOrder.Orders.getOrderByTaskOrder(event.newOrder).name
                savedStateHandle[AppConfig.ORDER_TYPE] = event.newOrder.orderType.name
            }
            is TasksEvent.UpdateTask -> updateTask(event.updatedTask)
            is TasksEvent.DeleteTask -> {
                recentlyDeletedTask = event.task
                deleteTask(event.task)
            }
            is TasksEvent.DeleteTasks -> {
                deleteTasks(event.taskIds)
            }
            is TasksEvent.DeleteAll -> {
                deleteAll()
            }
            is TasksEvent.RestoreTask -> {
                restoreTask()
            }
            is TasksEvent.SearchQueryChanged -> {
                event.query?.let {
                    updateSearchQuery(it)
                }
            }
            is TasksEvent.CompletingTask -> {
                Timber.d("CompletingTask ${event.completingTask}")
                completingTask = event.completingTask
            }
            is TasksEvent.CompletePhotoReport -> {
                Timber.d("CompletePhotoReport ${event.photoName}")
                completingTask?.let {
                    completePhotoReport(
                        it.copy(
                            reportPhoto = event.photoName, isCompleted = true
                        )
                    )
                }
                    ?: throw IllegalArgumentException("No task completing!")
            }
        }
    }

    private fun completePhotoReport(task: Task) {
        Timber.d("completePhotoReport for $task")
        viewModelScope.launch {
            when (tasksUseCases.updateTask(task)) {
                is Result.Success -> {
                    completingTask = null
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            when (tasksUseCases.updateTask(updatedTask)) {
                is Result.Success -> {
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun restoreTask() {
        viewModelScope.launch {
            val result = tasksUseCases.addTask(recentlyDeletedTask ?: return@launch)
            when (result) {
                is Result.Success -> {
                    recentlyDeletedTask = null
                    _userMessage.emit(R.string.tasks_task_restored)
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            when (tasksUseCases.deleteTask(task.id)) {
                is Result.Success -> {
                    _userMessage.emit(R.string.tasks_successfully_deleted_task)
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun deleteTasks(taskIds: List<Long>) {
        viewModelScope.launch {
            when (tasksUseCases.deleteTasks(taskIds)) {
                is Result.Success -> {
                    recentlyDeletedTask = null
                    _userMessage.emit(R.string.tasks_successfully_deleted_task)
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            when (tasksUseCases.deleteAllTasks()) {
                is Result.Success -> {
                    recentlyDeletedTask = null
                    _userMessage.emit(R.string.tasks_all_deleted)
                }
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun completeTask(updatedTask: Task) {
        viewModelScope.launch {
            when (tasksUseCases.completeTask(updatedTask)) {
                is Result.Success -> _userMessage.emit(R.string.tasks_task_completed)
                is Result.Error -> _userMessage.emit(R.string.tasks_unknown_error)
            }
        }
    }

    private fun updateSearchQuery(searchQuery: String) {
        if (lastSearchQuery == searchQuery) {
            return
        }
        lastSearchQuery = searchQuery
        searchQuery(searchQuery)
    }

    private fun searchQuery(searchQuery: String?) {
        if (searchQuery.isNullOrEmpty()) {
            viewModelScope.launch {
                loadTasks()
            }
            return
        }

        searchQueryJob?.cancel()
        searchQueryJob = viewModelScope.launch {
            _isLoading.update { true }
            tasksUseCases.searchQueryTasks(searchQuery).collect { tasks ->
                orderTasks(tasks, tasksState.value.taskOrder)
                _tasksResultState.update { tasks }
                _isLoading.update { false }
            }
        }
    }
}