package it.unipd.footbyfoot.fragments.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.findNavController
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.maps.manager.MapsManager
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment() {

    //The manager of the map, that also manages all the workouts updates
    lateinit var manager: MapsManager

    //Permissions to ask
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        //What to do when we have a result from activity
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
            }
        }
    }

    //Create the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        manager = MapsManager(this.activity as Activity)

        //If the service is running and we aren't on the finish view, go to finish view: the service has the state that defines what to do
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
    override fun onStart() {
        super.onStart()
        requirePermissions()
    }

    private fun requirePermissions() {
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED || checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            // Application can use position: start updating the map
            manager.startUpdateMap()

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            //If we should show a dialog to request permissions
            AlertDialog.Builder(requireActivity())
                .setMessage(
                    R.string.reason_to_update_permissions
                )
                .setPositiveButton(
                    R.string.request_permission_positive_button
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
            //If it's the first time, so we don't have to show a permission dialog
            permissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}