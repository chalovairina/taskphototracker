 package com.chalova.irina.todoapp.reminder_work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import kotlinx.coroutines.flow.first
import java.time.LocalDate

 class ReminderWorker(
     private val context: Context,
     workParams: WorkerParameters,
     private val authRepository: AuthRepository,
     private val tasksRepository: TaskRepository
    ): CoroutineWorker(context, workParams) {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()

        val userId = authRepository.getUserId() ?: return failure()

        val  currentTasks = tasksRepository.getTasks(userId).first()

        val tasksForTodayCount = currentTasks.stream()
            .filter { task ->
                DateTimeUtil.toLocalDate(task.dueDate) == LocalDate.now()
            }
            .count().toInt()

        val notifText =
            context.resources.getQuantityString(R.plurals.tasks_for_today, tasksForTodayCount, tasksForTodayCount)


        val notification = createTaskNotification(notifText)

        if (ContextCompat.checkSelfPermission(
            context,
            "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
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
            .setContentTitle(context.getString(R.string.check_on_todo_list))
            .setContentText(notifContent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = PRIORITY_MAX
        notification.setChannelId(NOTIFICATION_CHANNEL_ID)

        return notification.build()
    }

    private fun createNotificationChannel() {
        if (SDK_INT >= O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            channel.apply {
                enableLights(true)
                lightColor = RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                setSound(ringtoneManager, audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "todo_tasks_notifications"
        const val NOTIFICATION_CHANNEL_ID = "com.chalova.irina.todoapp.notifications"
        const val NOTIFICATION_ID = "todoapp_notification_id"
        const val NOTIFICATION_WORK = "todoapp_notification_work"
    }
}
