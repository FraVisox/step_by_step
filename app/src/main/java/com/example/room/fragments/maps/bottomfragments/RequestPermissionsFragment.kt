package com.example.room.fragments.maps.bottomfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.room.R
import com.example.room.fragments.maps.MapsFragment

//TO ADD MY LOCATION: https://developers.google.com/android/reference/com/google/android/gms/location/package-summary
class RequestPermissionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.maps_request_permissions, container, false)

        val button = view.findViewById<Button>(R.id.update_button)
        button.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_toEmpty)
            (parentFragment?.parentFragment as MapsFragment).askPermissions()
        }

        return view
    }

}