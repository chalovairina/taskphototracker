package com.chalova.irina.taskphototracker.reminder_work

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.chalova.irina.taskphototracker.MainActivity
import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.tasks.data.util.DateTimeUtil
import com.chalova.irina.taskphototracker.R
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate

class ReminderWorker(
    private val context: Context,
    workParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val tasksRepository: TaskRepository
) : CoroutineWorker(context, workParams) {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        Timber.d("doWork start")
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()

        val userId = authRepository.getUserId() ?: return failure()

        val currentTasks = tasksRepository.getTasksStream(userId).first()

        val tasksForTodayCount = currentTasks.count { task ->
            DateTimeUtil.toLocalDate(task.dueDate) == LocalDate.now()
        }

        val notifText =
            context.resources.getQuantityString(
                R.plurals.notification_tasks_for_today,
                tasksForTodayCount,
                tasksForTodayCount
            )

        val notification = createTaskNotification(notifText)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            failure()
        }

        notificationManager.notify(id, notification)

        return success()
    }

    private fun createTaskNotification(notifContent: String): Notification {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(context.getString(R.string.notification_check_on_todo_list))
            .setContentText(notifContent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = PRIORITY_MAX
        notification.setChannelId(NOTIFICATION_CHANNEL_ID)

        return notification.build()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "todo_tasks_notifications"
        const val NOTIFICATION_CHANNEL_ID = "com.chalova.irina.taskphototracker.notifications"
        const val NOTIFICATION_ID = "taskphototracker_notification_id"
        const val NOTIFICATION_WORK = "taskphototracker_notification_work"
    }
}
