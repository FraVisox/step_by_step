package com.example.room.fragments.maps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.room.R
import kotlin.properties.Delegates


class TrackWorkoutService: Service() {

    companion object {
        const val timeKey = "startTime"
        const val serviceId = 1
        private const val CHANNEL_ID = "Workout tracking"
    }

    private val binder = MyBinder()

    var startTime by Delegates.notNull<Long>()

    inner class MyBinder: Binder() {
        val service: TrackWorkoutService
            get() = this@TrackWorkoutService
    }

    override fun onCreate() {
        super.onCreate()

        //Create notification channel
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notification_channel_description)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder {
        startTime = intent?.getLongExtra(timeKey, 0) ?: 0
        return binder
    }

    fun startTracking() {
        // Build a notification with basic info
        val notificationBuilder: Notification.Builder = Notification.Builder(applicationContext, CHANNEL_ID)
        notificationBuilder.setContentTitle(getString(R.string.notification_title))
        notificationBuilder.setContentText(getString(R.string.notification_content))
        notificationBuilder.setSmallIcon(R.drawable.baseline_account_circle_24) //TODO: metti icona di notifica

        val notification = notificationBuilder.build()
        startForeground(serviceId, notification)


    }

    fun endTracking() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

}