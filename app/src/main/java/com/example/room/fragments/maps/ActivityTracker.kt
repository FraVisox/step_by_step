package com.example.room.fragments.maps

import android.location.Location
import java.util.Calendar

class ActivityTracker(private val manager: MapsManager) {

    private var track : Boolean = false

    //private lateinit var startTime : Calendar

    fun startActivity(loc : Location?) {
        if (loc == null) {
            return
        }
        //startTime = Calendar.getInstance()
        manager.addPointToLine(loc)
        track = true
    }

    fun finishActivity(loc : Location?) {
        if (loc == null) {
            return
        }

        //val endTime = Calendar.getInstance()
        track = false
        manager.addPointToLine(loc)
        //val time = endTime.timeInMillis-startTime.timeInMillis

        //TODO: store the polyline and the time

        manager.clearLine()
    }

    fun updatePolyline(current : Location) {
        if (!track) {
            return
        }
        manager.addPointToLine(current)
    }
}