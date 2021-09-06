package com.chari.ic.todoapp

import android.util.Log
import androidx.lifecycle.*
import com.chari.ic.todoapp.data.database.DatabaseResult
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: IDataStoreRepository,
    @Named("IoDispatcher") private val dispatchers: CoroutineDispatcher
): ViewModel() {
    val userLoggedIn = dataStoreRepository.readUserLoggedIn()

    fun writeUserLoggedIn(userLoggedIn: Boolean) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeUserLoggedIn(userLoggedIn)
        }
    }

     val getAllTasks: LiveData<List<ToDoTask>> = repository.cachedTasks

    val databaseStatus: LiveData<DatabaseResult<List<ToDoTask>>> = Transformations.map(getAllTasks) {
        cachedTasks ->
            if (cachedTasks == null) {
                DatabaseResult.Loading()
            } else if (cachedTasks.isEmpty()) {
                DatabaseResult.Empty()
            } else {
                DatabaseResult.Success(cachedTasks)
            }
    }

    fun insertTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.insertTask(toDoTask)
        }
    }

    fun updateTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.updateTask(toDoTask)
        }
    }

    fun deleteTask(toDoTask: ToDoTask) {
        viewModelScope.launch(dispatchers) {
            repository.deleteTask(toDoTask)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(dispatchers) {
            repository.deleteAll()
        }
    }

    fun searchDatabase(searchQuery: String) = repository.searchDatabase(searchQuery)
}