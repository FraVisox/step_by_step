package com.example.room.fragments.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.room.R

//TO ADD MY LOCATION: https://developers.google.com/android/reference/com/google/android/gms/location/package-summary
class RequestPermissionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.request_permissions, container, false)

        val button = view.findViewById<Button>(R.id.update_button)
        button.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_requestToEmpty)
            (parentFragment?.parentFragment as MapsFragment).askPermissions()
        }

        return view
    }

}