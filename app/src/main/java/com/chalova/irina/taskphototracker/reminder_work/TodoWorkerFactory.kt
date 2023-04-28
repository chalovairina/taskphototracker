package com.chalova.irina.taskphototracker.reminder_work

import androidx.work.DelegatingWorkerFactory
import com.chalova.irina.taskphototracker.di.app_scope.AppScope
import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import javax.inject.Inject

@AppScope
class TodoWorkerFactory @Inject constructor(
    tasksRepository: TaskRepository,
    authRepository: AuthRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(ReminderWorkerFactory(tasksRepository, authRepository))
    }
}