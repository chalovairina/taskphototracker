 package com.chari.ic.todoapp.reminder_work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.chari.ic.todoapp.MainActivity
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.repository.datastore.DataStoreRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.*

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
     @Assisted workParams: WorkerParameters,
    private val tasksRepository: ToDoRepository,
    private val dataStoreRepository: DataStoreRepository
    ): CoroutineWorker(context, workParams) {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "work started")
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()

        val userId = dataStoreRepository.readCurrentUserData().first().userId
        var  currentTasks = tasksRepository.cachedTasks(userId).first()

        var tasksForTodayCount = currentTasks.stream()
            .filter { task ->
                LocalDate.from(
                    LocalDateTime.ofInstant(task.dueDate, ZoneOffset.systemDefault()).toLocalDate()
                )
                    .equals(LocalDate.now())
            }
            .count()

        Log.d("Reminder Work", "tasks for today = $tasksForTodayCount")

        var notifText = if (tasksForTodayCount <= 0)
            context.getString(R.string.no_tasks_for_today)
        else String.format(
                context.getString(R.string.have_tasks_for_today),
                tasksForTodayCount
            )

        val notification = createTaskNotification(notifText)

        val channel = createNotificationChannel()

        if (channel != null && SDK_INT >= O) {
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification)

        return success()
    }

    private fun createTaskNotification(notifContent: String): Notification {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = getActivity(context, 0, intent, 0)

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

    /**
     * Creates notification channel which needs to be registered with NotificationManager
     * if device SDK is greater or equals Oreo
     */
    private fun createNotificationChannel(): NotificationChannel? {
        var channel: NotificationChannel? = null
        if (SDK_INT >= O) {
            channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
        }

        return channel
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "today_tasks"
        const val NOTIFICATION_CHANNEL_ID = "com.chari.ic.todoapp"
        const val NOTIFICATION_ID = "todoapp_notification_id"
        const val NOTIFICATION_WORK = "todoapp_notification_work"
    }
}
