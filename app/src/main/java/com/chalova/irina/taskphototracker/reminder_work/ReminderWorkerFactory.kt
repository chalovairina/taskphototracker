package com.chalova.irina.taskphototracker.reminder_work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository

class ReminderWorkerFactory(
    private val tasksRepository: TaskRepository,
    private val authRepository: AuthRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            ReminderWorker::class.java.name ->
                ReminderWorker(appContext, workerParameters, authRepository, tasksRepository)
            else ->
                null
        }
    }

}