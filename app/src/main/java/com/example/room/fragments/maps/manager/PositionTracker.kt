package com.example.room.fragments.maps.manager

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
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
import com.google.android.gms.tasks.Task

class PositionTracker(private val manager: MapsManager) {

    //Client to ask location to
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(manager.context)

    //Current location
    private var mCurrentLocation : Location? = null


    //The callback that updates the position every second
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations){
                mCurrentLocation = location
                manager.updatePosition(location)
            }
        }
    }

    //Function to start the tracking of the position
    fun startLocationTrack(accurate : Boolean) {
        //Create a request that asks for the position every second, with priority as passed
        val rr = LocationRequest.Builder(1000)
            .setPriority(if (accurate) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        //Add the request to the location settings
        val client: SettingsClient = LocationServices.getSettingsClient(manager.context)
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
                    exception.startResolutionForResult(manager.context, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationUpdates(rr : LocationRequest) {
        //Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                manager.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                manager.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        //Try to get last known location
        val result = fusedLocationClient.lastLocation
        result.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                mCurrentLocation = it.result
                manager.focusPosition(it.result)
            } else {
                //If it's not successful, get current location with the priority of the request
                fusedLocationClient.getCurrentLocation(
                    CurrentLocationRequest.Builder().setPriority(rr.priority).build(), null
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //If it's successful, update the position
                        mCurrentLocation = task.result
                        manager.focusPosition(task.result)
                    } else {
                        //Else, show the user a toast
                        val toast =
                            Toast.makeText(manager.context, "Position not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        }

        //Create a series of requests
        fusedLocationClient.requestLocationUpdates(
            rr,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun getCurrent() : Location? {
        return mCurrentLocation
    }
}