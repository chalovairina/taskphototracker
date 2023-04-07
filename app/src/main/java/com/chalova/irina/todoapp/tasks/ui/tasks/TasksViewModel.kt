package com.chalova.irina.todoapp.tasks.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.DatabaseResult
import com.chalova.irina.todoapp.tasks.utils.TaskOrder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TasksViewModel @AssistedInject constructor(
    private val taskRepository: TaskRepository,
    @Assisted val savedStateHandle: SavedStateHandle
): ViewModel() {

    @AssistedFactory
    interface TasksViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): TasksViewModel
    }

    private var userId: StateFlow<String?> = savedStateHandle
        .getStateFlow<String?>(AppConfig.USER_ID, null)

    private var taskOrder: Flow<TaskOrder> = combine(
        savedStateHandle
            .getStateFlow(AppConfig.TASK_ORDER, TaskOrder.Orders.Date.name),
        savedStateHandle
            .getStateFlow(AppConfig.ORDER_TYPE, TaskOrder.OrderTypes.Descending.name)) { taskOrder, orderType ->
        TaskOrder.getTaskOrderByName(taskOrder, orderType)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TaskOrder.Date(TaskOrder.OrderType.Descending))

    private val _tasksListState = MutableStateFlow<DatabaseResult<List<Task>>>(DatabaseResult.Empty())
    private val _searchQueryState = MutableStateFlow<String?>(null)
    private val _userMessage = MutableStateFlow<Int?>(null)

    val tasksState: StateFlow<TasksState> = combine(
        _tasksListState, taskOrder, _searchQueryState, _userMessage
    ) {
            tasksResult, taskOrder, searchQuery, userMessage ->
        TasksState(
            tasksResult = if (tasksResult is DatabaseResult.Success && tasksResult.data.isNullOrEmpty())
                DatabaseResult.Empty() else tasksResult,
            taskOrder = taskOrder,
            searchQuery = searchQuery,
            userMessage = userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TasksState())

    private var loadTasksJob: Job? = null

    init {

        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    _tasksListState.update {
                        DatabaseResult.Loading()
                    }
                    loadTasksJob?.cancel()
                    loadTasksJob = combine(taskRepository.getTasks(id), taskOrder) { tasks, order ->
                        orderTasks(tasks, order)
                    }.onEach { newTasks ->
                        _tasksListState.update {
                            DatabaseResult.Success(newTasks)
                        }
                    }.launchIn(viewModelScope)
                }
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
                    is TaskOrder.Priority -> {
                        tasks.sortedByDescending { it.priority }.toList()
                    }
                    is TaskOrder.Date -> tasks.sortedByDescending { it.dueDate }.toList()
                }
            }
        }
    }

    private var recentlyDeletedTask: Task? = null

    fun onEvent(event: TasksEvent) {
        when(event) {
            is TasksEvent.OnOrderChanged -> {
                if (event.newOrder::class == tasksState.value.taskOrder::class &&
                    event.newOrder.orderType::class == tasksState.value.taskOrder.orderType::class) {
                    return
                }
                savedStateHandle.set(AppConfig.TASK_ORDER, event.newOrder.orderTitle.name)
                savedStateHandle.set(AppConfig.ORDER_TYPE, event.newOrder.orderType.type.name)
            }
            is TasksEvent.DeleteTask -> {
                deleteTask(event.task)
                recentlyDeletedTask = event.task
            }
            is TasksEvent.DeleteTasks -> {
                deleteTasks(event.taskIds)
            }
            is TasksEvent.DeleteAll -> {
                deleteAll()
            }
            TasksEvent.RestoreTask -> {
                restoreTask()
            }
            is TasksEvent.OnSearchQueryChanged -> {
                searchQuery(event.query, tasksState.value.taskOrder)
            }
        }
    }

    private fun restoreTask() {
        viewModelScope.launch {
            taskRepository.insertTask(recentlyDeletedTask ?: return@launch)
            _userMessage.update { R.string.task_restored }
            recentlyDeletedTask = null
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
            _userMessage.update { R.string.successfully_added }
        }
    }

    fun insertTasks(tasks: List<Task>) {
        viewModelScope.launch {
            taskRepository.insertTasks(tasks)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(userId.value!!, task.id)
            _userMessage.update { R.string.successfully_deleted_task }
        }
    }

    private fun deleteTasks(taskIds: List<Long>) {
        viewModelScope.launch {
            taskRepository.deleteTasks(userId.value!!, taskIds)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            taskRepository.deleteAllTasks(userId.value!!)
            _userMessage.update { R.string.all_tasks_deleted }
        }
    }

    private var searchQueryJob: Job? = null
    private var lastSearchQuery: String? = null

    private fun searchQuery(searchQuery: String?, taskOrder: TaskOrder) {
        if (lastSearchQuery != null && lastSearchQuery == searchQuery) {
            return
        }
        lastSearchQuery = searchQuery

        searchQueryJob?.cancel()

        searchQueryJob = viewModelScope.launch {
            _tasksListState.update {
                DatabaseResult.Loading()
            }
            taskRepository.searchQuery(userId.value!!, "%$searchQuery%").collect { tasks ->
                orderTasks(tasks, taskOrder)
                _tasksListState.update {
                    DatabaseResult.Success(tasks)
                }
            }
        }
    }

    fun onUserMessageShown() {
        _userMessage.update { null }
    }
}

fun provideTasksFactory(
    assistedFactory: TasksViewModel.TasksViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }