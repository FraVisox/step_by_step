package it.unipd.footbyfoot.fragments.maps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.maps.manager.PositionLocationObserver
import it.unipd.footbyfoot.fragments.maps.manager.PositionTracker

class TrackWorkoutService: Service(), PositionLocationObserver {

    companion object {
        //Notification channel
        const val serviceId = 1
        //Pending intent request code
        const val requestCode = 0

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
            if (paused || !running) {
                //If it's paused, the startTime is only asked to get the current time, so we return a correct way
                return SystemClock.elapsedRealtime() - offset
            }
            return field
        }
    var distance : Float = 0F
    var locations : MutableList<LatLng?> = mutableListOf()

    //Used when the workout is paused
    private var offset : Long = 0

    //When the service is created, the notification channel is created (it's a safe operation, as if the channel already exists, no operation is performed)
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

    //Start a workout, if not already in progress
    fun startWorkout() {
        if (!running) {
            startTime = SystemClock.elapsedRealtime()
            running = true

            // Build a notification (the icon is the app's icon)
            val notificationBuilder: Notification.Builder =
                Notification.Builder(this, getString(R.string.channel_id))
            notificationBuilder
                .setShowWhen(false)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.mipmap.ic_launcher)

            //Make an intent if the user taps the notification using a launch intent for this package,
            //which means it will launch the root activity only if it is not running
            val pm = packageManager
            val launchIntent = pm.getLaunchIntentForPackage("it.unipd.footbyfoot")
            val contentIntent = PendingIntent.getActivity(this, requestCode, launchIntent, PendingIntent.FLAG_IMMUTABLE)
            notificationBuilder.setContentIntent(contentIntent)

            //Start foreground
            val notification = notificationBuilder.build()
            startForeground(serviceId, notification)

            //Start using the position
            PositionTracker.addObserver(this)
            PositionTracker.startLocationTrack(applicationContext)

            //Add the current location
            addThisLocation()
        }
    }

    //It can be paused only if it is running and not already paused
    fun pauseWorkout() {
        if (running && !paused) {
            offset = SystemClock.elapsedRealtime() - startTime
            paused = true

            //Remove the observation of positions
            PositionTracker.removeObserver(this)

            //Add the current location, and then null as a placeholder
            addThisLocation()
            locations.add(null)
        }
    }

    //It can be resumed only if it is running and paused
    fun resumeWorkout() {
        if (paused && running) {
            paused = false
            startTime = SystemClock.elapsedRealtime() - offset

            //Add the current location and start tracking
            addThisLocation()
            PositionTracker.addObserver(this)
        }
    }

    //End the workout: it doesn't clear the values of locations, distance and startTime
    fun stopWorkout() {
        if (running) {
            //Save the current time in the offset, as this will be used when we will ask the startTime after this function
            offset = SystemClock.elapsedRealtime() - startTime
            PositionTracker.removeObserver(this)
            addThisLocation()
            stopForeground(STOP_FOREGROUND_REMOVE)
            running = false
            paused = false
        }
    }

    //When it receives a new position, it updates the locations and the distance, if it isn't paused
    @Override
    override fun locationUpdated(loc: Location) {
        if (running && !paused) {
            val pos = LatLng(loc.latitude, loc.longitude)
            updateDistance(loc)
            locations.add(pos)
        }
    }

    //Clear the values of the workout
    fun clearWorkout() {
        if (!running) {
            distance = 0F
            startTime = 0
            offset = 0
            locations.clear()
        }
    }

    //If every user unbinds, stops itself
    override fun onUnbind(intent: Intent?): Boolean {
        PositionTracker.removeObserver(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return super.onUnbind(intent)
    }

    //Add the current location to the tracked locations
    private fun addThisLocation() {
        val current = PositionTracker.currentLocation
        if (current != null) {
            locations.add(LatLng(current.latitude, current.longitude))
        }
    }

    //Updates the distance using the last two locations
    private fun updateDistance(current: Location) {
        if (locations.isNotEmpty() && locations.last() != null) {
            val last = locations.last()!!
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