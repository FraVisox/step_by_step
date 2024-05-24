package com.example.room.fragments.maps.manager

import android.location.Location
import android.util.Log
import android.widget.TextView
import com.example.room.MainActivity
import com.example.room.RecordsApplication
import com.example.room.database.workout.Workout
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
    private var track : Boolean = false
    private var startTime : Long = 0
    private var distance : Double = 0.0

    companion object {
        private var oldTrack: Boolean = false
        private var oldStartTime: Long = 0
        private var oldDistance: Double = 0.0
    }

    fun startWorkout(loc: Location?, timeView: TextView, distanceView: TextView): Boolean {
        if (loc == null || startTime != 0.toLong()) {
            return false
        }
        startTime = Calendar.getInstance().timeInMillis

        this.timeView = timeView
        this.distanceView = distanceView

        manager.addPointToLine(loc)

        track = true

        updateTimeView()
        return true
    }

    private fun updateTimeView() {
        coroutine = scope.launch {
            while(isActive) {
                val millis: Long = Calendar.getInstance().timeInMillis - startTime
                Log.d("AAA", millis.toString())
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
        this.timeView = timeView
        this.distanceView = distanceView
        startTime = oldStartTime
        track = oldTrack
        distance = oldDistance
        updateTimeView()
    }

    fun restartWorkout(timeView: TextView, distanceView: TextView, time : Long, dd : Double, id: Int) {
        coroutine?.cancel()
        this.timeView = timeView
        this.distanceView = distanceView
        startTime = time
        distance = dd
        workoutId = id
        track = true
        updateTimeView()
    }
    fun pauseWorkout() {
        coroutine?.cancel()
        oldDistance = distance
        oldTrack = track
        oldStartTime = startTime
    }

    fun finishWorkout(loc : Location?) {
        //Cancel the updating of the timeView
        coroutine?.cancel()
        track = false

        //If the location was never taken
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
        if (!track) {
            return
        }
        updateDistance(current)
        manager.addPointToLine(current)
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