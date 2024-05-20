package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.room.R
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment() {

    //The tracker of the position: used to display the map and the current position
    lateinit var manager: MapsManager
    var distanceView: TextView? = null
    var timeView: TextView? = null

    //Permissions to ask
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()?.navigate(R.id.action_toStart)
                manager.startLocationTrack(true)
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()?.navigate(R.id.action_toStart)
                manager.startLocationTrack(false)
            }

            else -> {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // No location access granted, but there is a possibility to update
                    childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()
                        ?.navigate(R.id.action_emptyToRequest)
                    val toast =
                        Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    //Create the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        manager = MapsManager(this.activity as Activity, this)

        askPermissions()

        if (savedInstanceState != null && savedInstanceState.getBoolean(workoutStarted)) {
            manager.setWorkoutState(savedInstanceState.getLong(timeKey), savedInstanceState.getDouble(distanceKey), savedInstanceState.getInt(idKey))
        }

        return view
    }

    fun askPermissions() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED || checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            // Application can use position
            childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()?.navigate(R.id.action_toStart)
            manager.startLocationTrack(checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED)
            return
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            //This is the second time when system is about to show permission dialog.
            AlertDialog.Builder(requireActivity())
                .setMessage(
                    R.string.reason_to_update_permissions
                )
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    // Show permission dialog
                    permissions.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
                .create().show()
        } else {
            //Launch the requests of permissions
            permissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    //When it's created, put the map inside of it
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(manager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (timeView != null) {
            outState.putBoolean(workoutStarted, true)
            outState.putDouble(distanceKey, manager.getDistance())
            outState.putLong(timeKey, manager.getTime())
            outState.putInt(idKey, manager.getId())
        } else {
            outState.putBoolean(workoutStarted, true)
        }
    }

    companion object {
        const val workoutStarted = "workoutStarted"
        const val distanceKey = "distance"
        const val timeKey = "time"
        const val idKey = "id"
    }
}