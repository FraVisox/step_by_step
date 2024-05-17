package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Looper
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.tasks.Task
import kotlin.math.pow

class PositionTracker(val context: Activity) : OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    //Client to ask location to
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    //The callback that updates the position
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations){
                if (location != null) {
                    mCurrentLocation = location
                }
            }
        }
    }

    //Current location and if the permissions are granted
    private var mCurrentLocation : Location? = null
    var permissionGranted = false
    var accurate = false

    //The map
    private lateinit var map: GoogleMap
    private val positions: PolylineOptions =
        PolylineOptions().clickable(true).color(Color.parseColor("#FF0000")).startCap(
            RoundCap()
        )

    override fun onMapReady(googleMap: GoogleMap) {
        /*
        If Google Play services is not installed on the device, the user will be prompted to
        install it inside the SupportMapFragment. This method will only be triggered once the
        user has installed Google Play services and returned to the app.
        */
        map = googleMap
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            return
        }
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        startMap()
    }

    private fun startMap() {
        val loc = mCurrentLocation
        if (loc != null) {
            val pos = LatLng(loc.latitude, loc.longitude)
            map.moveCamera(CameraUpdateFactory.zoomTo(17F))
            map.animateCamera(CameraUpdateFactory.newLatLng(pos))
            //map.addPolyline(positions.add(pos))
        }
    }

    fun updateMap() {}

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(context, "Current location:\n$p0", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }
    fun startLocationTrack() {
        //https://developer.android.com/develop/sensors-and-location/location/change-location-settings
        // MY REQUEST
        // Rate in millisecond at which we want to receive updates
        val rr = LocationRequest.Builder(1000)
            .setPriority(if (accurate) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        //Get the current location settings
        val builder = LocationSettingsRequest.Builder().addLocationRequest(rr)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //When the task completes, we can check the settings by looking at the status of the LocSetResponse
        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            startLocationUpdates(rr)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationUpdates(rr : LocationRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val result = fusedLocationClient.lastLocation
        result.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                mCurrentLocation = it.result
                startMap()
            } else {
                fusedLocationClient.getCurrentLocation(
                    CurrentLocationRequest.Builder().setPriority(
                        Priority.PRIORITY_HIGH_ACCURACY).build(), null).addOnCompleteListener { task ->
                    // Got last known location. In some rare situations this can be null.
                    if (task.isSuccessful && task.result != null) {
                        mCurrentLocation = task.result
                        startMap()
                    } else {
                        val toast = Toast.makeText(context, "Position not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        }

        //Create a series of requests
        //https://developer.android.com/develop/sensors-and-location/location/request-updates
        fusedLocationClient.requestLocationUpdates(rr,
            locationCallback,
            Looper.getMainLooper())
    }

}