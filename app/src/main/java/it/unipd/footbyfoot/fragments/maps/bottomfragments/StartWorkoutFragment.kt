package it.unipd.footbyfoot.fragments.maps.bottomfragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.navigation.findNavController
import it.unipd.footbyfoot.R

class StartWorkoutFragment : Fragment() {

    companion object {
        //Used by the FinishWorkoutFragment
        var requestToStart = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps_bottom_start, container, false)

        //Set listener: go to finish only if the permissions are granted
        val start = view.findViewById<Button>(R.id.start_button)
        start.setOnClickListener {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), R.string.go_to_settings, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requestToStart = true
            view.findNavController()
                .navigate(R.id.action_startToFinish)
        }

        return view
    }
}