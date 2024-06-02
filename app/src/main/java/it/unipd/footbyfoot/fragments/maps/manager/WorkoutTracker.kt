package it.unipd.footbyfoot.fragments.maps.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.TextView
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.maps.TrackWorkoutService
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.R
import java.io.Serializable

class WorkoutTracker(private val manager: MapsManager) {

    //Views to update
    private lateinit var timeChronometer: Chronometer
    private var distanceView: TextView? = null

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
            timeChronometer.base = mService.startTime
            if (!TrackWorkoutService.paused) {
                timeChronometer.start()
            }
        }
        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
        }
    }

    fun setViews(time: Chronometer, distanceView: TextView) {
        //Connect the views
        this.timeChronometer = time
        this.distanceView = distanceView
    }

    fun startWorkout() {
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
            timeChronometer.base = mService.startTime
            timeChronometer.start()
        }
    }

    fun stopWorkout() {
        //If the workout only has one point, we save it anyway
        if (!mBound) {
            return
        }
        mService.stopWorkout()

        //Cancel the updating of the chronometer
        timeChronometer.base = SystemClock.elapsedRealtime()
        timeChronometer.stop()

        //Take time, distance and points
        val distance = mService.distance.toInt()
        val time = SystemClock.elapsedRealtime() - mService.startTime
        val positions : MutableList<LatLng?> = mutableListOf()
        positions.addAll(mService.locations)

        //Reset
        mService.clearWorkout()
        manager.context.applicationContext.unbindService(connection)
        manager.clearLine()

        //Start activity to save the workout
        val intent = Intent(manager.context, SaveWorkoutActivity::class.java)
        intent.putExtra(SaveWorkoutActivity.timeKey, time/1000)
        intent.putExtra(SaveWorkoutActivity.distanceKey, distance)
        intent.putExtra(SaveWorkoutActivity.positionsKey, positions as Serializable)
        manager.context.startActivity(intent)
    }

    fun updatePolyline(current : Location) {
        if (TrackWorkoutService.running && !TrackWorkoutService.paused) {
            manager.addPointToLine(current)
            updateDistance()
        }
    }

    private fun updateDistance() {
        distanceView?.post {
            distanceView?.text = manager.context.getString(
                R.string.distance_format,
                mService.distance.toInt()
            )
        }
    }
}