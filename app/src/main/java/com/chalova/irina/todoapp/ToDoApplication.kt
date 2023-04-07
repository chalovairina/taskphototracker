package com.chalova.irina.todoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import com.chalova.irina.todoapp.di.AppComponent
import com.chalova.irina.todoapp.di.DaggerAppComponent
import com.chalova.irina.todoapp.reminder_work.ReminderWorker
import javax.inject.Inject

class ToDoApplication: Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory()
            .create(this)
        appComponent.inject(this)

        WorkManager.initialize(
            this, workManagerConfiguration)

        createNotificationChannel()
    }

    @Inject lateinit var workerConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderWorker.NOTIFICATION_CHANNEL_ID,
                ReminderWorker.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            channel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                setSound(ringtoneManager, audioAttributes)
            }

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
}