package com.example.room.fragments.maps.manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

//The class that manages all the interactions and updates to the map
class MapsManager(val context: Activity) : OnMapReadyCallback, PositionLocationObserver {

    companion object {
        //Color of the polyline
        private val trackColor : Int = Color.parseColor("#FF0000")
        private val defaultOptions = PolylineOptions().color(trackColor).startCap(RoundCap()).endCap(RoundCap())
    }

    //Map
    private lateinit var map: GoogleMap

    /*
     * Booleans to initialize the map and start the tracking of the position
     */
    //Boolean to check if the map has been initialized.
    private var mapInitialized = false
    //Boolean to check if has been made a request while the map was not initialized
    private var requestMade = false

    /*
     * Utils to track position and workouts
     */
    //Tracker of position
    val positionTracker = PositionTracker(this)
    //Tracker of activities
    private val workoutTracker = WorkoutTracker(this)

    /*
     * Polyline and its options
     */
    //Polyline drawn
    var currPolyline : Polyline? = null
    var otherPolylines : MutableList<Polyline> = mutableListOf()
    //Options of the line to draw
    private var positions: PolylineOptions = PolylineOptions().color(trackColor).startCap(RoundCap()).endCap(RoundCap())

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapInitialized = true
        positionTracker.addObserver(this)
        if (requestMade) {
            startLocationTrack()
        }
    }

    //Start tracking of the position
    fun startLocationTrack() {
        //If the map has not been initialized, save the asking
        if (!mapInitialized) {
            requestMade = true
            return
        }
        //Check the permissions and start the tracking of the position
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            positionTracker.startLocationTrack()
        }
    }

    /*
     * Functions used by PositionTracker
     */
    //Used to focus on initial position
    fun focusPosition(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        map.moveCamera(CameraUpdateFactory.zoomTo(17F))
        map.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }
    //Updates the position in the activity: if so, we update the polyline if needed
    override fun locationUpdated(loc: Location) {
        workoutTracker.updatePolyline(loc)
    }

    /*
     * Functions used to manage the workouts
     */
    //Start a new workout
    fun startWorkout(timeView : TextView, distanceView: TextView): Boolean {
        startLocationTrack()
        Log.d("AAA", positionTracker.getCurrent().toString())
        if (positionTracker.getCurrent() == null) {
            //In this case, no workout could be initialized
            return false
        }
        return workoutTracker.startWorkout(positionTracker.getCurrent(), timeView, distanceView)
    }
    //End the current workout
    fun finishWorkout() {
        workoutTracker.finishWorkout(positionTracker.getCurrent())
    }
    //Pause the current workout
    fun pauseWorkout() {
        workoutTracker.pauseWorkout()
        if (currPolyline != null) {
            otherPolylines.add(currPolyline!!) //todo
            currPolyline = null
            positions = PolylineOptions().color(trackColor).startCap(RoundCap()).endCap(RoundCap())
        }
    }
    //Restart the workout
    fun restartWorkoutInDifferentFragment(timeView: TextView, distanceView: TextView) {
        startLocationTrack()
        workoutTracker.restartWorkoutInDifferentFragment(timeView, distanceView)
    }
    //Restart the workout
    fun restartWorkout() {
        startLocationTrack()
        workoutTracker.restartWorkout()
    }

    /*
     * Functions used to draw the track of the workout
     */
    //Adds a point to the line that is drawn
    fun addPointToLine(loc: Location) {
        Log.d("AAA", "un solo punto ${loc}")
        val pos = LatLng(loc.latitude, loc.longitude)
        val old = currPolyline
        currPolyline = map.addPolyline(positions.add(pos))
        old?.remove()
    }
    //Draw all current line
    fun drawCurrentTrack(locs: List<LatLng>) {
        if (locs.isEmpty()) {
            return
        }
        Log.d("AAA", "tanti punti ${locs}")
        val old = currPolyline
        currPolyline = map.addPolyline(positions.addAll(locs))
        old?.remove()
    }
    //Deletes the line drawn
    fun clearLine() {
        positions = PolylineOptions().color(trackColor).startCap(RoundCap()).endCap(RoundCap())
        currPolyline?.remove()
        for (p in otherPolylines) {
            p.remove()
        }
        otherPolylines.clear()
    }
}