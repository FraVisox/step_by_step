package com.example.room.fragments.workouts

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

//The class that manages all the interactions and updates to the map
class LittleMapManager : OnMapReadyCallback {

    //Map
    private var map: GoogleMap? = null

    //Polylines drawn
    private var polyline : Polyline? = null

    private var points: List<LatLng>? = null

    //Options of the line to draw
    private val positions: PolylineOptions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap()).endCap(RoundCap())

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (points != null) {
            createLine(points)
        }
    }

    //Used to focus on position initially
    private fun focusPosition(pos: LatLng) {
        map?.moveCamera(CameraUpdateFactory.zoomTo(15F))
        map?.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }

    //Adds a point to the line that is drawn
    fun createLine(locs: List<LatLng>?) {
        if (map == null) {
            points = locs
            return
        }
        focusPosition(locs!![0]) //TODO: migliora il !!
        polyline = map?.addPolyline(positions.addAll(locs))
    }

    //Deletes the line drawn
    fun clearLine() {
        polyline?.remove()
    }
}