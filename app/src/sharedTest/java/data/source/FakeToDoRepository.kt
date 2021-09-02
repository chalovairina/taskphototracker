package data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.Repository

class FakeToDoRepository: Repository {

    companion object {
        private var counter = 0
    }

    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            task.id = ++counter
            tasksData[counter] = task
        }

        refreshTasks()

    }

    private val tasksData = hashMapOf<Int, ToDoTask>()
    private val _cachedTasks = MutableLiveData<List<ToDoTask>>()
    override val cachedTasks: LiveData<List<ToDoTask>> = _cachedTasks

    private val _searchTasks = MutableLiveData<List<ToDoTask>>()
    val searchTasks: LiveData<List<ToDoTask>> = _searchTasks

    private suspend fun refreshTasks() {
        _cachedTasks.value = tasksData.values.toList()
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

    override suspend fun deleteAll() {
        counter = 0
        tasksData.clear()
        refreshTasks()
    }

    override fun searchDatabase(searchQuery: String): LiveData<List<ToDoTask>> {
        val resultList = arrayListOf<ToDoTask>()
        for (task in tasksData.values) {
            if (task.title.contains(searchQuery, true)) {
                resultList.add(task)
            }
        }
        _searchTasks.value = resultList

        return searchTasks
    }

    override suspend fun resetRepository() {
        // Clear all data to avoid test pollution.
        deleteAll()
    }
}