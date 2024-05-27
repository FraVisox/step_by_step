package com.example.room.fragments.maps.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

//Singleton design pattern
object PositionTracker {

    //Client to ask location to
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Current location
    var currentLocation : Location? = null
        private set

    //It is observed by other objects
    private val observers : MutableList<PositionLocationObserver>  = mutableListOf()

    //The callback that updates the position every second
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations){
                if (location != null) {
                    updateLocation(location)
                }
            }
        }
    }

    fun addObserver(obs: PositionLocationObserver) {
        observers.add(obs)
    }

    //There is no need to check if the element was present
    fun removeObserver(obs: PositionLocationObserver) {
        observers.remove(obs)
    }

    //Function to start the tracking of the position, called by the object that starts using it
    fun startLocationTrack(context: Context) {
        if (currentLocation != null) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        //Create a request that asks for the position every second
        val request = LocationRequest.Builder(1000).build()

        //Add the request to the location settings
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(LocationSettingsRequest.Builder()
            .addLocationRequest(request).build())

        //If the task has success, we now can initialize the location requests
        task.addOnSuccessListener {
            startLocationUpdates(request, context)
        }
    }

    private fun startLocationUpdates(request: LocationRequest, context: Context) {
        //Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        //Try to get last known location //TODO: casini se non attivo la posizione
        val result = fusedLocationClient.lastLocation
        result.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                //If it's successful, update the position
                updateLocation(it.result)
            } else {
                //If it's not successful, get current location
                fusedLocationClient.getCurrentLocation(
                    CurrentLocationRequest.Builder().build(), null
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        //If it's successful, update the position
                        updateLocation(task.result)
                    } else {
                        //Else, show the user a toast
                        val toast =
                            Toast.makeText(context, "Position not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        }

        //Create a series of requests
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocation(loc: Location) {
        currentLocation = loc
        for (obs in observers) {
            obs.locationUpdated(loc)
        }
    }
}