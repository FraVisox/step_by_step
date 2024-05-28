package it.unipd.footbyfoot.fragments.maps.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import it.unipd.footbyfoot.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
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
        Log.d("AAA", "start location track")


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        Log.d("AAA", "trovato fused")

        //Create a request that asks for the position every second
        val request = LocationRequest.Builder(1000).build()

        //Check the settings to see if location is enabled
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(LocationSettingsRequest.Builder()
            .addLocationRequest(request).build())

        Log.d("AAA", "buildato")


        //If the task has success, we now can initialize the location requests
        task.addOnSuccessListener {
            Log.d("AAA", "successoooo")
            startLocationUpdates(request, context)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult(): the result is taken in MapsManager //TODO
                    if (context is Activity) {
                        (context as MainActivity).showLocationDialog(exception)
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
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
            Log.d("AAA", "returning because no access to position")
            return
        }
        Log.d("AAA", "teee possiamo accedere")

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
        Log.d("AAA", "location updated")
        currentLocation = loc
        for (obs in observers) {
            obs.locationUpdated(loc)
        }
    }
}