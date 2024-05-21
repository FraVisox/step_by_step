package com.example.room.fragments.maps.bottomfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.room.R

//TO ADD MY LOCATION: https://developers.google.com/android/reference/com/google/android/gms/location/package-summary
class EmptyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_empty_fragment, container, false)
    }

}