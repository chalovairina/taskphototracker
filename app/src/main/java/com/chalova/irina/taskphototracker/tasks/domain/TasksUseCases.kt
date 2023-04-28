package com.chalova.irina.taskphototracker.tasks.domain

class TasksUseCases(
    val addTask: AddTask,
    val updateTask: UpdateTask,
    val deleteTask: DeleteTask,
    val deleteTasks: DeleteTasks,
    val deleteAllTasks: DeleteAllTasks,
    val completeTask: CompleteTask,
    val getTask: GetTask,
    val getTasks: GetTasks,
    val searchQueryTasks: GetSearchQueryTasks
)