package com.example.room.fragments.maps.bottomfragments

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
import com.example.room.R
import com.example.room.fragments.maps.MapsFragment

class StartWorkoutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.maps_start_workout, container, false)

        val start = view.findViewById<Button>(R.id.start_button)
        start.setOnClickListener {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED) {
                val toast = Toast.makeText(requireContext(), R.string.go_to_settings, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }

            view.findNavController()
                .navigate(R.id.action_startToFinish)
        }

        return view
    }
}