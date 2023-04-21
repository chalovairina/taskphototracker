package com.chalova.irina.todoapp.tasks.domain

class TasksUseCases(
    val addTask: AddTask,
    val deleteTask: DeleteTask,
    val deleteTasks: DeleteTasks,
    val deleteAllTasks: DeleteAllTasks,
    val getTask: GetTask,
    val getTasks: GetTasks,
    val searchQueryTasks: GetSearchQueryTasks
)