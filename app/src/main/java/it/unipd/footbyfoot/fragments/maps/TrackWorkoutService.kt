package it.unipd.footbyfoot.fragments.maps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.maps.manager.PositionLocationObserver
import it.unipd.footbyfoot.fragments.maps.manager.PositionTracker
import com.google.android.gms.maps.model.LatLng

class TrackWorkoutService: Service(), PositionLocationObserver {

    companion object {
        //Notification channel
        const val serviceId = 1

        //Booleans that tell if the workout is in progress or not: the setter is private
        var running = false
            private set
        var paused = false
            private set
    }

    //Binder used to bind to external user
    inner class TrackServiceBinder: Binder() {
        val service: TrackWorkoutService
            get() = this@TrackWorkoutService
    }
    //When an external user binds to this service
    override fun onBind(intent: Intent?): IBinder {
        return TrackServiceBinder()
    }

    //Current startTime, distance and locations covered
    var startTime : Long = 0
        get() {
            if (paused) {
                return SystemClock.elapsedRealtime() - offset
            }
            return field
        }
    var distance : Float = 0F
    var locations : MutableList<LatLng?> = mutableListOf()

    //Used for pausing the workout
    private var offset : Long = 0

    //When the service is created, the notification channel is created
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            getString(R.string.channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notification_channel_description)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    //If the component is paused, we don't allow to start another workout
    fun startWorkout() {
        if (!running) {
            startTime = SystemClock.elapsedRealtime()
            running = true

            // Build a notification //TODO: fai notifica meglio
            val notificationBuilder: Notification.Builder =
                Notification.Builder(this, getString(R.string.channel_id))
            notificationBuilder.setContentTitle(getString(R.string.notification_title))
            notificationBuilder.setContentText(getString(R.string.notification_content))
            notificationBuilder.setSmallIcon(R.drawable.baseline_directions_run_24)

            //Start foreground
            val notification = notificationBuilder.build()
            startForeground(serviceId, notification)

            //Start using the position
            PositionTracker.addObserver(this)
            PositionTracker.startLocationTrack(applicationContext)

            //Start this workout
            addThisLocation()
        }
    }

    fun pauseWorkout() {
        if (running && !paused) {
            offset = SystemClock.elapsedRealtime() - startTime
            paused = true

            //Remove the observation of positions
            PositionTracker.removeObserver(this)

            addThisLocation()
            locations.add(null)
        }
    }

    fun resumeWorkout() {
        if (paused && running) {
            paused = false
            startTime = SystemClock.elapsedRealtime() - offset
            addThisLocation()
            PositionTracker.addObserver(this)
        }
    }

    fun endWorkout() {
        if (running) {
            addThisLocation()
            PositionTracker.removeObserver(this)
            stopForeground(STOP_FOREGROUND_REMOVE)
            running = false
            paused = false
        }
    }

    //When it receives a new position, it updates the locations and the distance, if it isn't paused
    override fun locationUpdated(loc: Location) {
        if (running && !paused) {
            val pos = LatLng(loc.latitude, loc.longitude)
            updateDistance(loc)
            locations.add(pos)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        PositionTracker.removeObserver(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return super.onUnbind(intent)
    }

    private fun addThisLocation() {
        val current = PositionTracker.currentLocation
        if (current != null) {
            locations.add(LatLng(current.latitude, current.longitude))
        }
    }

    fun clearWorkout() {
        running = false
        paused = false
        distance = 0F
        startTime = 0
        offset = 0
        locations.clear()
    }

    private fun updateDistance(current: Location) {
        if (locations.isNotEmpty() && locations.last() != null) {
            val last = locations.last()
            val result = FloatArray(1)
            Location.distanceBetween(
                last!!.latitude,
                last.longitude,
                current.latitude,
                current.longitude,
                result
            )
            distance += result[0]
        }
    }

}