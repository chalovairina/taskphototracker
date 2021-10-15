package com.chari.ic.todoapp

import android.util.Log
import androidx.lifecycle.*
import com.chari.ic.todoapp.data.database.DatabaseResult
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
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
    // to keep data about currently updated task
    val _taskToUpdate = MutableLiveData<ToDoTask?>(null)
    val taskToUpdate: LiveData<ToDoTask?> = _taskToUpdate

    // write / read for current user in DataStore
    val currentUser = dataStoreRepository.readCurrentUserData().asLiveData()
    fun writeCurrentUserData(userId: String, userName: String, userMobile: Long,
                             userImageUrl: String, userEmail: String, fcmToken: String) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeCurrentUserData(userId, userName, userMobile,
                userImageUrl, userEmail, fcmToken)
        }
    }
    fun writeCurrentUserName(userName: String) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeCurrentUserName(userName)
        }
    }
    fun writeCurrentUserMobile(userMobile: Long) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeCurrentUserMobile(userMobile)
        }
    }
    fun writeCurrentUserImageUrl(userImageUrl: String) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeCurrentUserImageUrl(userImageUrl)
        }
    }
    fun writeCurrentUserFcmToken(fcmToken: String) {
        viewModelScope.launch(dispatchers) {
            dataStoreRepository.writeCurrentUserFcmToken(fcmToken)
        }
    }

    // user tasks from local database
//     val cachedTasks = getAllTasks()
//     private fun getAllTasks(): LiveData<List<ToDoTask>> {
//        var currentUserId = ""
//        viewModelScope.launch(dispatchers) {
//            currentUserId = dataStoreRepository.readCurrentUserData().first().userId
//        }
//
//        return repository.cachedTasks(
//            currentUserId
//        ).asLiveData()
//    }

    val cachedTasks: LiveData<List<ToDoTask>> = Transformations.switchMap(currentUser) { user ->
        val currentUserId = user.userId

        repository.cachedTasks(
            currentUserId
        ).asLiveData()
    }

    // database request status to reflect in UI
    val databaseStatus: LiveData<DatabaseResult<List<ToDoTask>>> = Transformations.map(cachedTasks) {
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
            repository.deleteAll(
                currentUser.value?.userId ?: ""
            )
        }
    }

    fun searchDatabase(searchQuery: String) =
            repository.searchDatabaseByUserId(searchQuery,
                currentUser.value?.userId ?: ""
            ).asLiveData()

}