package com.chalova.irina.toodapp.domain.tasks

import com.chalova.irina.taskphototracker.tasks.data.Task
import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import kotlin.random.Random

class TasksProvider {

    private val testUserId = "userId"
    private val testDueDate = Instant.now()

    private val tasks = mutableListOf<Task>()
    private val _tasksFlow: MutableStateFlow<List<Task>> = MutableStateFlow(tasks)
    private val tasksFlow: Flow<List<Task>> = _tasksFlow


    init {
        fillTasks()
    }

    fun getTasks() = tasks
    val getTasksFlow = tasksFlow

    suspend fun addTask(task: Task) {
        val updated = mutableListOf<Task>()
        updated.addAll(tasks)
        updated.add(task)
        _tasksFlow.emit(updated)
    }

    suspend fun updateTask(task: Task) {
        val updated = mutableListOf<Task>()
        updated.addAll(tasks)
        updated.find { it.id == task.id }?.let {
            updated.remove(it)
            updated.add(task)
        }
        _tasksFlow.emit(updated)
    }

    suspend fun deleteTasks() {
        tasks.clear()
        _tasksFlow.emit(emptyList())
    }

    suspend fun deleteTask(id: Long) {
        val updated = mutableListOf<Task>()
        updated.addAll(tasks)
        updated.removeIf { it.id == id }
        _tasksFlow.emit(updated)
    }

    suspend fun deleteTasks(ids: List<Long>) {
        val updated = mutableListOf<Task>()
        updated.addAll(tasks)
        updated.removeIf { ids.contains(it.id) }
        _tasksFlow.emit(updated)
    }

    private fun fillTasks() {
        ('a'..'f').forEachIndexed { i, c ->
            tasks.add(
                i, Task(
                    id = i.toLong(),
                    userId = testUserId, title = c.toString(),
                    priority = Priority.values()[Random(0).nextInt(Priority.values().size)],
                    dueDate = testDueDate.plusMillis(i.toLong())
                )
            )
        }
    }
}