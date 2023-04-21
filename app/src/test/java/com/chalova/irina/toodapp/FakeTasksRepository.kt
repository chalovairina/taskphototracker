package com.chalova.irina.toodapp

import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.time.Instant

class FakeTasksRepository
    : TaskRepository {

    private var tasks = mutableListOf<Task>()
    private val _tasksFlow: MutableStateFlow<List<Task>> = MutableStateFlow(tasks)
    private val tasksFlow: Flow<List<Task>> = _tasksFlow
    override suspend fun getTasksStream(userId: String): Flow<List<Task>> {
        return tasksFlow
    }

    override suspend fun getTask(userId: String, taskId: Long): Task? {
        return _tasksFlow.value.find { it.id == taskId }
    }

    override suspend fun insertTask(task: Task) {
        val tasks = mutableListOf<Task>()
        tasks.addAll(_tasksFlow.value)

        tasks.add(task)

        _tasksFlow.update { tasks }
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        val newTasks = mutableListOf<Task>()
        newTasks.addAll(_tasksFlow.value)

        newTasks.addAll(tasks)

        println("insert tasks " + newTasks.size)
        _tasksFlow.update { newTasks.toList() }
    }

    override suspend fun updateTask(
        userId: String,
        taskId: Long,
        title: String,
        priority: Priority,
        description: String,
        dueDate: Instant
    ) {

    }

    override suspend fun completeTask(userId: String, taskId: Long) {
        val tasks = mutableListOf<Task>()
        tasks.addAll(_tasksFlow.value)

        val task = _tasksFlow.value.find { it.id == taskId }
        val completedTask = task?.copy(isCompleted = true)
        if (task != null) {
            tasks.removeIf { it.id == task.id }
            tasks.add(completedTask!!)
        }

        _tasksFlow.update { tasks }
    }

    override suspend fun deleteTask(userId: String, taskId: Long) {
        val tasks = mutableListOf<Task>()
        tasks.addAll(_tasksFlow.value)

        tasks.removeIf { it.id == taskId }

        _tasksFlow.update { tasks.toList() }

        println(tasks.size)
        println(this.tasks.size)
        println(_tasksFlow.value.size)
    }

    override suspend fun deleteTasks(userId: String, taskIds: List<Long>) {
        val tasks = mutableListOf<Task>()
        tasks.addAll(_tasksFlow.value)

        taskIds.forEach { id ->
            tasks.removeIf { it.id == id }
        }
        _tasksFlow.update { tasks }
    }

    override suspend fun deleteAllTasks(userId: String) {
        _tasksFlow.update { emptyList() }
    }

    override suspend fun getSearchQueryStream(userId: String, searchQuery: String): Flow<List<Task>> {
        val query = searchQuery.replace('%', ' ').trim()

        return flowOf(
            _tasksFlow.value.filter { it.title.contains(query) })
    }

}
