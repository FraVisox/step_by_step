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
import java.util.Calendar

class TrackWorkoutService: Service(), PositionLocationObserver {

    //Way to bind to external user
    inner class MyBinder(val firstUse: Boolean): Binder() {
        val service: TrackWorkoutService
            get() = this@TrackWorkoutService
    }

    companion object {
        //Way to pass start time to this object
        const val timeKey = "startTime"

        //Notification channel
        const val serviceId = 1
        private const val CHANNEL_ID = "Workout tracking"
    }

    //Way to receive updates of position
    private lateinit var positionTracker : PositionTracker

    //Current startTime and distances
    private var startTime : Long = 0
    private var distance : Float = 0F
    private var locations : MutableList<LatLng> = mutableListOf()

    //Utilities used for pausing the workout
    private var previousTime : Long = 0
    private var previousDistance : Float = 0F
    private var previousLocations : MutableList<LatLng> = mutableListOf()

    //Used to update views
    fun getStartTime(): Long {
        return startTime - previousTime
    }
    fun getDistance(): Float {
        return distance + previousDistance
    }
    fun getLocations(): MutableList<LatLng>  {
        val locs = locations
        locs.addAll(previousLocations)
        return locs
    }

    //When the service is created, we create the notification channel
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notification_channel_description)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    //When an external user binds to this service
    override fun onBind(intent: Intent?): IBinder {
        return if (startTime == 0L) {
            startTime = Calendar.getInstance().timeInMillis
            MyBinder(true)
        } else {
            MyBinder(false)
        }
    }

    fun startTracking(positionTracker: PositionTracker) {
        // Build a notification
        val notificationBuilder: Notification.Builder = Notification.Builder(applicationContext, CHANNEL_ID)
        notificationBuilder.setContentTitle(getString(R.string.notification_title))
        notificationBuilder.setContentText(getString(R.string.notification_content))
        notificationBuilder.setSmallIcon(R.drawable.baseline_directions_run_24)

        val notification = notificationBuilder.build()
        startForeground(serviceId, notification)

        //Start using the position
        positionTracker.addObserver(this)

        //Start this workout
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

    fun pauseWorkout() {
        previousTime += (Calendar.getInstance().timeInMillis-startTime)
        previousDistance += distance
        positionTracker.removeObserver(this)

        val curr = positionTracker.getCurrent()
        if (curr != null) {
            locations.add(LatLng(curr.latitude, curr.longitude))
        }
        previousLocations.addAll(locations)

        //Clear everything but the startTime, as this workout hasn't finished yet
        distance = 0F
        locations.clear()
    }

    fun restartWorkout() {
        startTime = Calendar.getInstance().timeInMillis

        val current = positionTracker.getCurrent()
        if (current!= null) {
            locations.add(LatLng(current.latitude, current.longitude))
        }

        positionTracker.addObserver(this)
    }

    //When we receive a new position
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