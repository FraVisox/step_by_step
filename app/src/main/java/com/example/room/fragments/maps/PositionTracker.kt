package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.tasks.Task
import kotlin.math.pow

class PositionTracker(val context: Activity) : OnMapReadyCallback {

    //Client to ask location to
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    //Current location
    private var mCurrentLocation : Location? = null

    //Map
    private lateinit var map: GoogleMap

    private var track : Boolean = false

    fun startLine() {
        val loc = mCurrentLocation
        if (loc != null) {
            val pos = LatLng(loc.latitude, loc.longitude)
            polylines.add(map.addPolyline(positions.add(pos)))
        }
        track = true
    }

    fun endLine() {
        track = false
        val loc = mCurrentLocation
        if (loc != null) {
            val pos = LatLng(loc.latitude, loc.longitude)
            val polyline = map.addPolyline(positions.add(pos))



            //TODO: store the polyline



            positions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap())
            polyline.remove()
            polylines.forEach {
                it.remove()
            }
            polylines.clear()
        }
    }

    private var positions: PolylineOptions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap())

    private var polylines : MutableList<Polyline> = mutableListOf()


    //The callback that updates the position: it updates the position and draw the polyline, if needed
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations){
                if (location != null) {
                    mCurrentLocation = location
                    updatePolyline(location)
                }
            }
        }
    }

    //When the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        startMap()
    }

    //Start the map
    private fun startMap() {
        val loc = mCurrentLocation
        if (loc != null) {
            val pos = LatLng(loc.latitude, loc.longitude)
            map.moveCamera(CameraUpdateFactory.zoomTo(17F))
            map.animateCamera(CameraUpdateFactory.newLatLng(pos))
        }
    }

    //Function to start the tracking of the position
    fun startLocationTrack(accurate : Boolean) {
        //Create a request that asks for the position every second, with priority as passed
        val rr = LocationRequest.Builder(1000)
            .setPriority(if (accurate) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        //Take the current location settings
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        //Add the request to the location settings
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(LocationSettingsRequest.Builder().addLocationRequest(rr).build())

        //If the task has success, we now can initialize the location requests
        task.addOnSuccessListener {
            startLocationUpdates(rr)
        }

        //If the task fails, try to ask the user TODO: how to do it
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    //context.showLocationDialog()
                    exception.startResolutionForResult(context, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationUpdates(rr : LocationRequest) {
        //Check if permissions are granted: if not, return
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        //Try to get last known location
        val result = fusedLocationClient.lastLocation
        result.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                mCurrentLocation = it.result
                startMap()
            } else {
                //If it's not successful, get current location with the priority of the request
                fusedLocationClient.getCurrentLocation(CurrentLocationRequest.Builder().setPriority(rr.priority).build(), null).addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        //If it's successful, update the position
                        mCurrentLocation = task.result
                        startMap()
                    } else {
                        //Else, show the user a toast
                        val toast = Toast.makeText(context, "Position not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        }

        //Create a series of requests
        fusedLocationClient.requestLocationUpdates(rr,
            locationCallback,
            Looper.getMainLooper())
    }

    fun updatePolyline(current : Location) {
        if (!track) {
            return
        }
        val pos = LatLng(current.latitude, current.longitude)
        polylines.add(map.addPolyline(positions.add(pos)))
    }

}