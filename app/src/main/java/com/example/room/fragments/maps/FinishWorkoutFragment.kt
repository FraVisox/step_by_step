package com.example.room.fragments.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.room.R

//TO ADD MY LOCATION: https://developers.google.com/android/reference/com/google/android/gms/location/package-summary
class FinishWorkoutFragment : Fragment() {

    private lateinit var fragment: MapsFragment

    //Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.finish_activity_maps, container, false)

        fragment = (parentFragment?.parentFragment as MapsFragment)

        //Initialize the text views used in MapsFragment
        fragment.timeView = view.findViewById(R.id.time_tv)
        fragment.distanceView = view.findViewById(R.id.km_tv)

        val finish = view.findViewById<Button>(R.id.finish_button)
        finish.setOnClickListener {
            fragment.manager.finishActivity()
            fragment.timeView?.text = (R.string.initial_time).toString()
            fragment.timeView?.text = (R.string.initial_km).toString()
            fragment.timeView = null
            fragment.distanceView = null

            view.findNavController()
                .navigate(R.id.action_finishToStart)
        }

        return view
    }

}