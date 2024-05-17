package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.room.R
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment() {

    //The tracker of the position: used to display the map and the current position
    lateinit var manager: MapsManager

    //Permissions to ask
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                manager.startLocationTrack(true)
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                manager.startLocationTrack(false)
            }

            else -> {
                // No location access granted. TODO: manage this case
                val toast = Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    //Create the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        manager = MapsManager(this.activity as Activity)
        askPermissions()

        Log.d("AAA", "createview")

        return view
    }

    private fun askPermissions() {
        //Launch the requests of permissions
        permissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //When it's created, put the map inside of it
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(manager)
        Log.d("AAA", "viewcreated")
    }

}