package com.chari.ic.todoapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeToDoRepository @Inject constructor(): Repository {

    companion object {
        private var counter = 0
    }

    private val tasksData = hashMapOf<Int, ToDoTask>()
    private val _cachedTasks = MutableLiveData<List<ToDoTask>>(emptyList())
    override fun cachedTasks(userId: String): Flow<List<ToDoTask>> = _cachedTasks.asFlow()

    private val _searchTasks = MutableLiveData<List<ToDoTask>>(emptyList())
    val searchTasks: Flow<List<ToDoTask>> = _searchTasks.asFlow()

    private suspend fun refreshTasks() {
        _cachedTasks.postValue(tasksData.values.toList())
    }

    override suspend fun insertTask(toDoTask: ToDoTask) {
        tasksData[++counter] = toDoTask
        toDoTask.id = counter
        refreshTasks()
    }

    override suspend fun updateTask(toDoTask: ToDoTask) {
        tasksData[toDoTask.id] = toDoTask
        refreshTasks()
    }

    override suspend fun deleteTask(toDoTask: ToDoTask) {
        if (tasksData.containsKey(toDoTask.id)) {
            tasksData.remove(toDoTask.id)
        }
        refreshTasks()
    }

    override suspend fun deleteAll(userId: String) {
        counter = 0
        tasksData.clear()
        refreshTasks()
    }

    override fun searchDatabaseByUserId(searchQuery: String, userId: String): Flow<List<ToDoTask>> {
        val resultList = arrayListOf<ToDoTask>()
        for (task in tasksData.values) {
            if (task.title.contains(searchQuery, true)) {
                resultList.add(task)
            }
        }
        _searchTasks.postValue(resultList)

        return searchTasks
    }

    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            task.id = ++counter
            tasksData[counter] = task
        }
        refreshTasks()
    }

    override suspend fun resetRepository(userId: String) {
        // Clear all data to avoid test pollution.
        deleteAll(userId)
    }
}