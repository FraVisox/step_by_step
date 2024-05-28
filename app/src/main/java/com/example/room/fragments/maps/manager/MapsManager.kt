package com.example.room.fragments.maps.manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getString
import com.example.room.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

//The class that manages all the interactions and updates to the map
class MapsManager(val context: Activity) : OnMapReadyCallback, PositionLocationObserver {

    //Color of the polyline
    private val trackColor : Int = Color.parseColor(getString(context, R.color.colorPrimary)) //TODO: prendi da stringa

    //Returns the options to construct the polyline
    private fun defaultOptions(): PolylineOptions {
        return PolylineOptions().color(trackColor).startCap(RoundCap()).endCap(RoundCap()) //TODO: cambia start e end cap
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
    //Boolean to check if it has already been zoomed the area that we need
    private var first = true //TODO: salva first e fai in modo che si faccia il focus appena ho la posizione

    /*
     * Tracker that manages the binding to the service
     */
    private val workoutTracker = WorkoutTracker(this)

    /*
     * Polylines drawn
     */
    var currPolyline : Polyline? = null
    var otherPolylines : MutableList<Polyline> = mutableListOf()
    //Options of current polyline
    private var options = defaultOptions()

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapInitialized = true
        PositionTracker.addObserver(this)
        if (requestMade) {
            startUpdateMap()
        }
    }

    //Start tracking of the position
    fun startUpdateMap() {
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
            PositionTracker.startLocationTrack(context.applicationContext)
        }
    }

    //Function called by PositionTracker: update the polyline, if needed
    override fun locationUpdated(loc: Location) {
        if (first) {
            focusPosition(loc)
            first = false
        }
        workoutTracker.updatePolyline(loc)
    }

    //Used to focus on initial position
    private fun focusPosition(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        if (map.cameraPosition.zoom == 5F)
            map.moveCamera(CameraUpdateFactory.zoomTo(17F)) //TODO: prendi 17 da qualche altra parte
        map.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }

    fun setViews(time: Chronometer, distanceView: TextView) {
        workoutTracker.setViews(time, distanceView)
    }

    /*
     * Functions used to manage the workouts
     */
    //Start a new workout, returns false if the position was not found
    fun startWorkout(): Boolean {
        if (PositionTracker.currentLocation == null) {
            //In this case, no workout could be initialized
            return false
        }
        addPointToLine(PositionTracker.currentLocation!!)
        workoutTracker.startWorkout()
        return true
    }
    //End the current workout
    fun finishWorkout() {
        workoutTracker.finishWorkout()
    }
    //Pause the current workout
    fun pauseWorkout() {
        workoutTracker.pauseWorkout()
        if (currPolyline != null) {
            otherPolylines.add(currPolyline!!)
            currPolyline = null
            options = defaultOptions()
        }
    }
    //Resume the workout
    fun resumeWorkout() {
        addPointToLine(PositionTracker.currentLocation!!)
        workoutTracker.resumeWorkout()
    }

    /*
     * Functions used to draw the track of the workout
     */
    //Adds a point to the line that is drawn
    fun addPointToLine(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        currPolyline?.remove()
        options.add(pos)
        if (mapInitialized)
            currPolyline = map.addPolyline(options)
    }
    //Draw all current line
    fun drawCurrentTrack(locs: List<LatLng?>) {
        //TODO: problema con questa funzione
        if (locs.isEmpty()) {
            return
        }
        currPolyline?.remove()
        otherPolylines.forEach {
            it.remove()
        }
        Log.d("AAA", locs.toString())
        for (p in locs) {
            if (p == null) {
                if (currPolyline != null) {
                    otherPolylines.add(currPolyline!!)
                }
                currPolyline = null
                options = defaultOptions()
                Log.d("AAA", "resetting $currPolyline")
                continue
            }
            currPolyline?.remove()
            currPolyline = map.addPolyline(options.add(p))
            Log.d("AAA", "drawing $currPolyline")
        }
    }
    //Deletes the lines drawn
    fun clearLine() {
        options = defaultOptions()
        currPolyline?.remove()
        otherPolylines.forEach {
            it.remove()
        }
        otherPolylines.clear()
    }
}