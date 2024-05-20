package com.example.room.fragments.maps

import android.location.Location
import android.util.Log
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController
import com.example.room.ActivityApplication
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.database.activities.Workout
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.Calendar
import java.util.Date
import java.util.logging.Handler

class WorkoutTracker(private val manager: MapsManager) {

    private var track : Boolean = false
    private lateinit var coroutine : Job

    private var startTime : Long = 0
    private var distance : Double = 0.0 //in meters

    fun startActivity(loc : Location?) {
        if (loc == null) {
            return
        }
        startTime = Calendar.getInstance().timeInMillis
        manager.addPointToLine(loc)
        coroutine = (manager.context.application as ActivityApplication).applicationScope.launch {
            while(true) {
                val millis: Long = Calendar.getInstance().timeInMillis - startTime
                var seconds: Int = (millis / 1000).toInt()
                var minutes: Int = (seconds / 60)
                val hours: Int = (minutes/60)
                minutes %= 60
                seconds %= 60
                (manager.context.application as ActivityApplication).applicationScope.launch(Dispatchers.Main) {
                    manager.fragment.timeView.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}" //TODO: metti i minuti e secondi con gli zeri davanti
                }
                Log.d("AAA", "ugo boss")
                delay(500)
            }
        }
        track = true
    }

    fun finishActivity(loc : Location?) {
        if (loc == null || manager.polyline == null) {
            return
        }

        coroutine.cancel()
        val endTime = Calendar.getInstance()
        track = false
        manager.addPointToLine(loc)
        updateDistance(loc)
        val time = endTime.timeInMillis-startTime

        val positions : List<LatLng> = manager.polyline?.points?.toList() ?: listOf()

        //TODO: store the polyline positions and the time and the kms
        (manager.context as MainActivity).activityViewModel.insertWorkout(Workout(2,1,"user name", time/1000, distance.toInt(), Date()), positions)

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
            manager.fragment.distanceView.text = "${distance.toInt()}m"
        }
    }
}