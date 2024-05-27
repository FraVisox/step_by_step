package com.example.room.fragments.maps.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.TextView
import com.example.room.fragments.maps.SaveWorkoutActivity
import com.example.room.fragments.maps.TrackWorkoutService
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class WorkoutTracker(private val manager: MapsManager) {

    //Views to update
    private lateinit var timeChronometer: Chronometer
    private lateinit var distanceView: TextView

    //Service that tracks the workouts: it's a service as it will survive the killing of the Activity
    private lateinit var mService: TrackWorkoutService
    private var mBound = false

    // Callbacks for service binding (ServiceConnection interface)
    private val connection = object : ServiceConnection
    {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TrackWorkoutService.TrackServiceBinder
            mService = binder.service
            mBound = true

            if (TrackWorkoutService.running) {
                manager.drawCurrentTrack(mService.locations)
            } else {
                mService.startWorkout()
            }
            updateChronometer()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
        }
    }

    fun startWorkout(time: Chronometer, distanceView: TextView) {
        //Connect the views
        this.timeChronometer = time
        this.distanceView = distanceView

        //Create the service that will be used to track the workout: the chronometer will be updated when we receive the callback
        val intent = Intent(manager.context, TrackWorkoutService::class.java)
        manager.context.applicationContext.bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun pauseWorkout() {
        if (mBound) {
            mService.pauseWorkout()
            timeChronometer.stop()
        }
    }

    fun resumeWorkout() {
        if (mBound) {
            mService.resumeWorkout()
            updateChronometer()
        }
    }

    fun finishWorkout() {
        if (!mBound || (manager.currPolyline == null && manager.otherPolylines.isEmpty())) {
            return
        }
        mService.endWorkout()

        //Cancel the updating of the chronometer
        timeChronometer.stop()

        //Take time, distance and points
        val distance = mService.distance.toInt()
        val time = SystemClock.elapsedRealtime() - mService.startTime
        val positions : MutableList<LatLng?> = mService.locations

        //Reset
        mService.endWorkout()
        manager.context.applicationContext.unbindService(connection)
        manager.clearLine()

        //Start activity to save the workout
        val intent = Intent(manager.context, SaveWorkoutActivity::class.java)
        intent.putExtra(SaveWorkoutActivity.timeKey, time/1000)
        intent.putExtra(SaveWorkoutActivity.distanceKey, distance)
        intent.putExtra(SaveWorkoutActivity.positionsKey, positions as Serializable)
        manager.context.startActivity(intent)
    }

    fun updatePolyline(current : Location) { //TODO: forse migliora qua
        if (TrackWorkoutService.running && !TrackWorkoutService.paused) {
            manager.addPointToLine(current)
            updateDistance()
        }
    }

    private fun updateChronometer() {
        timeChronometer.base = mService.startTime
        timeChronometer.start()
    }

    private fun updateDistance() {
        distanceView.post {
            distanceView.text = "${mService.distance.toInt()}m" //TODO: prendi da stringhe
        }
    }
}