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

    //Id of this workout
    private var workoutId : Int = 1 //TODO: migliora (che succede se chiudo e riapro?)

    //For this workout
    private var startTime : Long = 0
    private var distance : Double = 0.0

    private lateinit var mService: TrackWorkoutService
    private var mBound = false

    // Callbacks for service binding (ServiceConnection interface)
    private val connection = object : ServiceConnection
    {
        override fun onServiceConnected(className: ComponentName, service: IBinder)
        {
            val binder = service as TrackWorkoutService.MyBinder
            mService = binder.service
            mBound = true

            mService.startTracking()
            startTime = mService.startTime

            updateTimeView()
        }
        override fun onServiceDisconnected(name: ComponentName) { mBound = false }
    }

    fun startWorkout(loc: Location?, timeView: TextView, distanceView: TextView): Boolean {
        //If there is no location or the track is already going on, return false
        if (loc == null || mBound) {
            return false
        }

        //Connect the views
        this.timeView = timeView
        this.distanceView = distanceView

        //Create the service that will be used to track the workout
        val intent = Intent(manager.context, TrackWorkoutService::class.java)
        intent.putExtra(TrackWorkoutService.timeKey, Calendar.getInstance().timeInMillis)
        manager.context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        return true
    }

    fun pauseWorkout() {
        if (mBound) {
            manager.context.unbindService(connection)
            mBound = false
        }

        //Cancel the updating of the timeView
        coroutine?.cancel()
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

    fun restartWorkout(timeView: TextView, distanceView: TextView) {
        //Connect the views
        this.timeView = timeView
        this.distanceView = distanceView

        //Bind to the service that is tracking the workout
        val intent = Intent(manager.context, TrackWorkoutService::class.java)
        manager.context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun finishWorkout(loc : Location?) {
        if (mBound) {
            mService.endTracking()
            manager.context.unbindService(connection)
            mBound = false
        }

        //Cancel the updating of the timeView
        coroutine?.cancel()

        //If the location was never taken, return
        if (loc == null || manager.polyline == null) {
            return
        }

        //Take endingTime
        val endTime = Calendar.getInstance().timeInMillis

        //Add this point to location and update the distance
        manager.addPointToLine(loc)
        updateDistance(loc)

        //Take time and points
        val time = endTime-startTime
        val positions : List<LatLng> = manager.polyline?.points?.toList() ?: listOf()

        (manager.context as MainActivity).recordsViewModel.insertWorkout(Workout(workoutId, 1,"Activity $workoutId", time/1000, distance.toInt(), Date()), positions)
        workoutId++

        distance = 0.0
        startTime = 0
        manager.clearLine()
    }

    fun updatePolyline(current : Location) {
        if (mBound) {
            updateDistance(current)
            manager.addPointToLine(current)
        }
    }

    private fun updateDistance(current: Location) {
        val last = manager.polyline?.points?.last()
        if (last != null) {
            val result : FloatArray = FloatArray(1)
            Location.distanceBetween(
                last.latitude,
                last.longitude,
                current.latitude,
                current.longitude,
                result
            )
            distance += result[0] //TODO: migliora
            distanceView.post {
                distanceView.text = "${distance.toInt()}m"
            }
        }

    }
}