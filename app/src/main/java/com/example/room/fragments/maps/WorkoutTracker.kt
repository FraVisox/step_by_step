package com.example.room.fragments.maps

import android.location.Location
import android.util.Log
import com.example.room.MainActivity
import com.example.room.database.activities.Workout
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar
import java.util.Date

class WorkoutTracker(private val manager: MapsManager) {

    private var track : Boolean = false

    private lateinit var startTime : Calendar
    private var distance : Double = 0.0 //in meters

    fun startActivity(loc : Location?) {
        if (loc == null) {
            return
        }
        startTime = Calendar.getInstance()
        manager.addPointToLine(loc)
        track = true
    }

    fun finishActivity(loc : Location?) {
        if (loc == null || manager.polyline == null) {
            return
        }

        val endTime = Calendar.getInstance()
        track = false
        manager.addPointToLine(loc)
        val last = manager.polyline?.points?.last()
        if (last != null) {
            val result : FloatArray = FloatArray(1)
            Location.distanceBetween(
                last.latitude,
                last.longitude,
                loc.latitude,
                loc.longitude,
                result
            )
            distance += result[0]
        }
        val time = endTime.timeInMillis-startTime.timeInMillis
        Log.d("AAA", distance.toString())

        val positions : List<LatLng> = manager.polyline?.points?.toList() ?: listOf()

        //TODO: store the polyline positions and the time and the kms
        (manager.context as MainActivity).activityViewModel.insertWorkout(Workout(2,1,"aaaa", time, distance/1000, Date()), positions)

        distance = 0.0
        manager.clearLine()
    }

    fun updatePolyline(current : Location) {
        if (!track) {
            return
        }
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
            distance += result[0]
        }
        manager.addPointToLine(current)
    }
}