package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.room.R
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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsFragment : Fragment() {

    //Activity that contains this fragment
    private lateinit var context : Activity

    //Tools used for creating requests
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val callback : CallBack = CallBack(this)

    //Current location and if the permissions are granted
    private var mCurrentLocation : Location? = null
    private var permissionGranted = false

    //Permissions to ask
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                permissionGranted = true
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                permissionGranted = true
            }

            else -> {
                // No location access granted.
                permissionGranted = false
            }
        }
    }

    //Create the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        context = this.activity as Activity

        //Initialize the fused location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        //Create a location callback that only changes mCurrentLocation and updates the map
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    if (location != null) {
                        mCurrentLocation = location
                        callback.updateMap()
                    }
                }
            }
        }

        //Start getting location
        getLocationRequest()

        return view
    }

    //When it's created, put the map inside of it
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    //This is the callback called when the map is ready. googleMap is an object GoogleMap
    class CallBack(val fragment: MapsFragment) : OnMapReadyCallback {
        private val default : LatLng = LatLng(45.4, 11.8)
        private lateinit var map : GoogleMap
        override fun onMapReady(googleMap : GoogleMap) {
            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera.
             * In this case, we just add a marker near Sydney, Australia.
             * If Google Play services is not installed on the device, the user will be prompted to
             * install it inside the SupportMapFragment. This method will only be triggered once the
             * user has installed Google Play services and returned to the app.
             */
            map = googleMap
            updateMap()
        }

        fun updateMap() {
            var pos = default
            val loc = fragment.mCurrentLocation
            if (loc != null) {
                pos = LatLng(loc.latitude, loc.longitude)
                map.addMarker(MarkerOptions().position(pos).title("Your position"))
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(pos))
        }
    }

    private fun getPermissions() {
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        if (!permissionGranted)
            permissions.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
            ))
    }

    private fun getLocationRequest() {
        //https://developer.android.com/develop/sensors-and-location/location/change-location-settings
        // MY REQUEST
        // Rate in millisecond at which we want to receive updates
        val rr = LocationRequest.Builder(1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        //Get the current location settings
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(rr)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //When the task completes, we can check the settings by looking at the status of the LocSetResponse
        task.addOnSuccessListener { locationSettingsResponse ->
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
                    exception.startResolutionForResult(context,
                        1) //TODO: il request code può essere qualsiasi cosa
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationUpdates(rr : LocationRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissions()
        }

        //Get the last known location https://developers.google.com/android/reference/com/google/android/gms/location/CurrentLocationRequest.Builder
        fusedLocationClient.getCurrentLocation(CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).setMaxUpdateAgeMillis(Long.MAX_VALUE).build(), null).addOnSuccessListener { location : Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                mCurrentLocation = location
                callback.updateMap()
            }
        }

        //Create a series of requests
        //https://developer.android.com/develop/sensors-and-location/location/request-updates
        fusedLocationClient.requestLocationUpdates(rr,
            locationCallback,
            Looper.getMainLooper())
    }


}