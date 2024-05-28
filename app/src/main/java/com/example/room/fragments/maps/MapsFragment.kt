package com.example.room.fragments.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.findNavController
import com.example.room.R
import com.example.room.fragments.maps.manager.MapsManager
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment() {

    //TODO: come fare se la posizione viene impostata dopo il fragment viene creato

    //The tracker of the position: used to display the map and the current position
    lateinit var manager: MapsManager

    //Permissions to ask
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
                manager.startUpdateMap()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                manager.startUpdateMap()
            }

            else -> {
                //Nothing happens
                Toast.makeText(context, "Access to position not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Create the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.maps_fragment, container, false)

        manager = MapsManager(this.activity as Activity)

        if (TrackWorkoutService.running && childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()
                ?.currentDestination?.id != R.id.finish_workout_fragment) {
            childFragmentManager.findFragmentById(R.id.bottom_fragment)?.findNavController()
                ?.navigate(R.id.action_startToFinish)
        }

        return view
    }

    //When it's created, put the map inside of it
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(manager)
    }

    //Everytime it's started, check permissions
    override fun onResume() {
        super.onResume()
        requirePermissions()
    }

    private fun requirePermissions() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED || checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            // Application can use position
            Log.d("AAA", "lessgooo")
            manager.startUpdateMap()

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            //Show permissions dialog
            AlertDialog.Builder(requireActivity())
                .setMessage(
                    R.string.reason_to_update_permissions
                )
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
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
}