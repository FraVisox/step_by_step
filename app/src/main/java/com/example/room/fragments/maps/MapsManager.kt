package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.internal.zzag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

//The class that manages all the interactions and updates to the map
class MapsManager(val context: Activity) : OnMapReadyCallback {

    //Map
    private lateinit var map: GoogleMap

    //Boolean to check if the map has been initialized. TODO: l'alternativa è mettere la mappa come possibile null
    private var mapInitialized = false

    //Boolean to check if has been made a request while the map was not initialized
    private var requestMade = false

    //Boolean to check if has been made a request while the map was not initialized
    private var requestAccurate = false

    //Tracker of position
    private val positionTracker = PositionTracker(this)

    //Tracker of activities
    private val activityTracker = WorkoutTracker(this)

    //Polyline drawn
    var polyline : Polyline? = null  //TODO: meglio più polyline?

    //Options of the line to draw
    private var positions: PolylineOptions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap()).endCap(RoundCap())

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapInitialized = true
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        if (requestMade) {
            startLocationTrack(requestAccurate)
        }
    }

    //Used to focus on position initially
    fun focusPosition(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        map.moveCamera(CameraUpdateFactory.zoomTo(17F))
        map.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }

    //Start a new activity
    fun startActivity() {
        activityTracker.startActivity(positionTracker.getCurrent())
    }

    //End the current activity
    fun finishActivity() {
        activityTracker.finishActivity(positionTracker.getCurrent())
    }

    //Start tracking of the position
    fun startLocationTrack(accurate : Boolean) {
        if (!mapInitialized) {
            requestMade = true
            requestAccurate = accurate
            return
        }
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true
        positionTracker.startLocationTrack(accurate)
    }

    //Updates the position in the activity
    fun updatePosition(loc : Location) {
        activityTracker.updatePolyline(loc)
    }

    //Adds a point to the line that is drawn
    fun addPointToLine(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        val old = polyline
        polyline = map.addPolyline(positions.add(pos))
        old?.remove()
    }

    //Deletes the line drawn
    fun clearLine() {
        positions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap()).endCap(RoundCap())
        polyline?.remove()
    }
}