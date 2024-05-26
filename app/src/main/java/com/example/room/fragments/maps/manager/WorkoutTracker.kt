package com.example.room.fragments.maps.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import com.example.room.MainActivity
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.database.workout.Workout
import com.example.room.fragments.maps.TrackWorkoutService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class WorkoutTracker(private val manager: MapsManager) {

    //Coroutine to track time
    private var coroutine : Job? = null

    private val scope = (manager.context.application as RecordsApplication).applicationScope //todo: cambia lo scope

    private lateinit var timeView : TextView
    private lateinit var distanceView: TextView

    //For this workout
    private var startTime : Long = 0

    private lateinit var mService: TrackWorkoutService
    private var mBound = false
    private var track = false

    // Callbacks for service binding (ServiceConnection interface)
    private val connection = object : ServiceConnection
    {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TrackWorkoutService.MyBinder
            mService = binder.service
            mBound = true

            mService.startTracking(manager.positionTracker)

            setTracker()
            updateTimeView()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
        }
    }

    private fun setTracker() {
        startTime = mService.getStartTime()
        manager.drawCurrentTrack(mService.getLocations())
    }

    fun startWorkout(loc: Location?, timeView: TextView, distanceView: TextView): Boolean {
        //If there is no location or the track is already going on, return false
        if (mBound || loc == null) {
            return false
        }

        //Connect the views
        this.timeView = timeView
        this.distanceView = distanceView

        track = true

        //Create the service that will be used to track the workout
        val intent = Intent(manager.context, TrackWorkoutService::class.java)
        manager.context.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        return true
    }

    fun pauseWorkout() {
        //Cancel the updating of the timeView
        coroutine?.cancel()
        if (mBound) {
            mService.pauseWorkout()
        }
        track = false
    }

    fun restartWorkout() {
        if (mBound) {
            mService.restartWorkout()
            startTime = mService.getStartTime()
            updateTimeView()
        }
        track = true
    }

    fun restartWorkoutInDifferentFragment(timeView: TextView, distanceView: TextView) {
        //Connect the views
        this.timeView = timeView
        this.distanceView = distanceView

        //Bind to the service that is tracking the workout
        val intent = Intent(manager.context, TrackWorkoutService::class.java)
        manager.context.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        track = true
    }

    fun finishWorkout(loc : Location?) {
        //Cancel the updating of the timeView
        coroutine?.cancel()
        track = false

        var distance = 0

        if (mBound) {
            distance = mService.getDistance().toInt()
            mService.endTracking()
            manager.context.applicationContext.unbindService(connection)
        }

        //If the location was never taken, return
        if (loc == null || (manager.currPolyline == null && manager.otherPolylines.isEmpty())) {
            return
        }

        //Take time and points and workout ID
        val time = Calendar.getInstance().timeInMillis-startTime
        val positions : MutableList<LatLng> = manager.currPolyline?.points?.toMutableList() ?: mutableListOf()
        manager.otherPolylines.forEach {
            positions.add(RecordsViewModel.positionPlaceholder)
            positions.addAll(it.points)
        }
        val thisID = (manager.context.application as RecordsApplication).workoutId

        (manager.context as MainActivity).recordsViewModel.insertWorkout(Workout(thisID, 1,"Activity $thisID", time/1000, distance, Date()), positions)
        (manager.context.application as RecordsApplication).workoutId++

        //Reset
        startTime = 0
        manager.clearLine()
    }

    fun updatePolyline(current : Location) {
        if (track) {
            if (mBound) {
                updateDistance()
            }
            manager.addPointToLine(current)
        }
    }

    private fun updateTimeView() {
        coroutine = scope.launch {
            while(isActive) {
                //Get the time and update the text view
                val millis: Long = Calendar.getInstance().timeInMillis - startTime
                var seconds: Int = (millis / 1000).toInt()
                var minutes: Int = (seconds / 60)
                val hours: Int = (minutes/60)
                minutes %= 60
                seconds %= 60
                timeView.post {
                    timeView.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
                }
                delay(500)
            }
        }
    }

    private fun updateDistance() {
        distanceView.post {
            distanceView.text = "${mService.getDistance().toInt()}m"
        }
    }
}