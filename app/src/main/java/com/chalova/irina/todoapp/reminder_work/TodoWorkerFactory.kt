package com.chalova.irina.todoapp.reminder_work

import androidx.work.DelegatingWorkerFactory
import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import javax.inject.Inject

@AppScope
class TodoWorkerFactory @Inject constructor(
    tasksRepository: TaskRepository,
    authRepository: AuthRepository
): DelegatingWorkerFactory() {

    init {
        addFactory(ReminderWorkerFactory(tasksRepository, authRepository))
    }
}