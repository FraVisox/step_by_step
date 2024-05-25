package com.example.room.fragments.maps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.room.R
import com.example.room.fragments.maps.manager.PositionLocationObserver
import com.example.room.fragments.maps.manager.PositionTracker
import com.google.android.gms.maps.model.LatLng


class TrackWorkoutService: Service(), PositionLocationObserver {

    companion object {
        const val timeKey = "startTime"
        const val serviceId = 1
        private const val CHANNEL_ID = "Workout tracking"
    }

    private val binder = MyBinder()
    private lateinit var positionTracker : PositionTracker

    var startTime : Long = 0
    var distance : Float = 0F
    var locations : MutableList<LatLng> = mutableListOf()

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
        Log.d("AAA", "creato")
    }

    override fun onBind(intent: Intent?): IBinder {
        if (startTime == 0L) {
            startTime = intent?.getLongExtra(timeKey, 0) ?: 0
        }
        Log.d("AAA", startTime.toString())
        return binder
    }

    fun startTracking(positionTracker: PositionTracker) {
        // Build a notification with basic info
        val notificationBuilder: Notification.Builder = Notification.Builder(applicationContext, CHANNEL_ID)
        notificationBuilder.setContentTitle(getString(R.string.notification_title))
        notificationBuilder.setContentText(getString(R.string.notification_content))
        notificationBuilder.setSmallIcon(R.drawable.baseline_account_circle_24) //TODO: metti icona di notifica

        val notification = notificationBuilder.build()
        startForeground(serviceId, notification)

        positionTracker.addObserver(this)

        val current = positionTracker.getCurrent()
        if (current!= null) {
            locations.add(LatLng(current.latitude, current.longitude))
        }

        this.positionTracker = positionTracker
    }

    fun endTracking() {
        positionTracker.removeObserver(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun locationUpdated(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)

        updateDistance(loc)

        locations.add(pos)
    }

    private fun updateDistance(current: Location) {
        if (locations.isNotEmpty()) {
            val last = locations.last()

            val result = FloatArray(1)
            Location.distanceBetween(
                last.latitude,
                last.longitude,
                current.latitude,
                current.longitude,
                result
            )
            distance += result[0]
        }
    }

}