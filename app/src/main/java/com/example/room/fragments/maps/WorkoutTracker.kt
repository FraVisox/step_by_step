package com.example.room.fragments.maps

import android.location.Location
import android.util.Log
import com.example.room.ActivityApplication
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.database.workout.Workout
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class WorkoutTracker(private val manager: MapsManager) {

    //Coroutine to track time
    private lateinit var coroutine : Job

    private val scope = (manager.context.application as ActivityApplication).applicationScope

    //Id of this workout
    private var workoutId : Int = 1 //TODO: migliora (che succede se chiudo e riapro?)

    //For this workout
    private var track : Boolean = false
    private var startTime : Long = 0
    private var distance : Double = 0.0

    fun startActivity(loc : Location?) {
        if (loc == null) {
            return
        }
        startTime = Calendar.getInstance().timeInMillis
        manager.addPointToLine(loc)
        coroutine = scope.launch {
            while(true) {
                val millis: Long = Calendar.getInstance().timeInMillis - startTime
                var seconds: Int = (millis / 1000).toInt()
                var minutes: Int = (seconds / 60)
                val hours: Int = (minutes/60)
                minutes %= 60
                seconds %= 60
                scope.launch(Dispatchers.Main) {
                    manager.fragment.timeView?.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}" //TODO: metti i minuti e secondi con gli zeri davanti
                }
                delay(500)
            }
        }
        track = true
    }

    fun setWorkoutState(time : Long, dd : Double, id: Int) {
        startTime = time
        distance = dd
        workoutId = id
        track = true
        coroutine = scope.launch {
            while(true) {
                val millis: Long = Calendar.getInstance().timeInMillis - startTime
                var seconds: Int = (millis / 1000).toInt()
                var minutes: Int = (seconds / 60)
                val hours: Int = (minutes/60)
                minutes %= 60
                seconds %= 60
                scope.launch(Dispatchers.Main) {
                    manager.fragment.timeView?.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}" //TODO: metti i minuti e secondi con gli zeri davanti
                }
                delay(500)
            }
        }
    }

    fun getDistance(): Double {
        return distance
    }

    fun getId(): Int {
        return workoutId
    }

    fun getTime(): Long {
        return startTime
    }

    fun finishActivity(loc : Location?) {
        if (loc == null || manager.polyline == null) {
            return
        }
        //Take endingTime
        val endTime = Calendar.getInstance().timeInMillis

        //Cancel the updating of the textview
        coroutine.cancel()
        track = false

        //Add this point to location and update the distance
        manager.addPointToLine(loc)
        updateDistance(loc)

        //Take time and points
        val time = endTime-startTime
        val positions : List<LatLng> = manager.polyline?.points?.toList() ?: listOf()

        (manager.context as MainActivity).activityViewModel.insertWorkout(Workout(workoutId, 1,"Activity $workoutId", time/1000, distance.toInt(), Date()), positions)
        workoutId++

        distance = 0.0
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
            manager.fragment.distanceView?.text = "${distance.toInt()}m"
        }
    }
}