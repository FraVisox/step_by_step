package it.unipd.footbyfoot.fragments.maps.manager

import android.Manifest
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
import it.unipd.footbyfoot.R

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
        val obj = observers.find { it.javaClass == obs.javaClass }
        if (obj != null) {
            observers.remove(obj)
        }
        observers.add(obs)
    }

    //There is no need to check if the element was present
    fun removeObserver(obs: PositionLocationObserver) {
        observers.remove(obs)
    }

    //Function to start the tracking of the position, called by the object that starts using it
    fun startLocationTrack(context: Context) {
        if (currentLocation != null) {
            updateLocation(currentLocation!!)
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        //Create a request that asks for the position every second
        val request = LocationRequest.Builder(2500).build()

        //Check the settings to see if location is enabled
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(LocationSettingsRequest.Builder()
            .addLocationRequest(request).build())


        //If the task has success, we now can initialize the location requests
        task.addOnSuccessListener {
            startLocationUpdates(request, context)
        }

        //If the settings aren't enabled
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    //Show a location dialog in MainActivity
                    if (context is MainActivity) {
                        context.showLocationDialog(exception)
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    //Start getting location updates
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

        //Try to get last known location
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
                            Toast.makeText(context, context.getString(R.string.position_not_found), Toast.LENGTH_SHORT)
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
            Log.d("AAA", "updated location for $obs")
            obs.locationUpdated(loc)
        }
    }
}